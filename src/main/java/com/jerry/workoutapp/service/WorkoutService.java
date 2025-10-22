package com.jerry.workoutapp.service;

import com.jerry.workoutapp.dto.SetRequest;
import com.jerry.workoutapp.dto.WorkoutExerciseResponse;
import com.jerry.workoutapp.dto.WorkoutResponse;
import com.jerry.workoutapp.entity.*;
import com.jerry.workoutapp.repository.*;
import com.jerry.workoutapp.util.Validation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final WorkoutExerciseSetRepository workoutExerciseSetRepository;
    private final Validation validation;

    public WorkoutService(WorkoutRepository workoutRepository,
                          UserRepository userRepository,
                          ExerciseRepository exerciseRepository,
                          WorkoutExerciseRepository workoutExerciseRepository,
                          WorkoutExerciseSetRepository workoutExerciseSetRepository,
                          Validation validation) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.workoutExerciseSetRepository = workoutExerciseSetRepository;
        this.validation = validation;
    }

    @Transactional
    public WorkoutResponse createWorkout(String workoutName) {
        // Fetch the user by email
        User user = getAuthenticatedUser();

        // Check if a workout with this name already exists for this user
        boolean exists = workoutRepository
                .findByNameAndUser_UserId(workoutName, user.getUserId()).isPresent();
        if (exists) {
            throw new RuntimeException("Workout with name '" + workoutName
                    + "' already exists for this user.");
        }

        Workout workout = new Workout(workoutName, user);
        Workout savedWorkout = workoutRepository.save(workout);
        return convertToResponse(savedWorkout);
    }

    @Transactional
    public WorkoutResponse addExerciseToWorkout(Long workoutId, Long exerciseId,
                                                Integer restTime, Integer orderIndex,
                                                List<SetRequest> sets) {  // NY PARAMETER!

        // Authenticate user
        User user = getAuthenticatedUser();

        // Fetch workout and verify ownership
        Workout workout = workoutRepository.findByWorkoutIdAndUser_UserId(workoutId, user.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "Workout not found or you don't have permission to modify it"));

        // Fetch exercise
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + exerciseId));

        // Validate exercise not already in workout
        validateExerciseNotInWorkout(workout, exerciseId);

        // Fetch existing exercises for order management
        List<WorkoutExercise> existingExercises = workoutExerciseRepository
                .findByWorkout_WorkoutIdOrderByOrderIndexAsc(workoutId);

        // Handle orderIndex logic (BEFORE we create WorkoutExercise)
        if (orderIndex == null) {
            // If orderIndex isn´t set, set it to the next position
            if (existingExercises.isEmpty()) {
                orderIndex = 0;
            } else {
                // Find highest order_index and add 1
                orderIndex = existingExercises.stream()
                        .map(WorkoutExercise::getOrderIndex)
                        .max(Integer::compareTo)
                        .orElse(-1) + 1;
            }
        } else {
            // If orderIndex is set and already occupied, move all other exercises down
            for (WorkoutExercise we : existingExercises) {
                if (we.getOrderIndex() >= orderIndex) {
                    we.setOrderIndex(we.getOrderIndex() + 1);
                }
            }
            workoutExerciseRepository.saveAll(existingExercises);
            workoutExerciseRepository.flush();
        }

        // Create WorkoutExercise (WITHOUT sets/reps)
        WorkoutExercise workoutExercise = new WorkoutExercise(
                workout,
                exercise,
                restTime != null ? restTime : 60,  // Default 60 sekunder vila
                orderIndex
        );

        // Save WorkoutExercise first (need ID before we can create sets)
        workoutExercise = workoutExerciseRepository.save(workoutExercise);
        workoutExerciseRepository.flush();  // Force db to generate ID

        // Create all sets for this exercise
        if (sets != null && !sets.isEmpty()) {
            for (int i = 0; i < sets.size(); i++) {
                SetRequest setReq = sets.get(i);
                WorkoutExerciseSet set = new WorkoutExerciseSet(
                        workoutExercise,
                        i + 1,  // setNumber (1-indexed, 1,2,3 and so on)
                        setReq.getTargetReps(),
                        setReq.getTargetWeight()
                );
                workoutExerciseSetRepository.save(set);
            }
        }

        // Reload workout with all relations
        Workout savedWorkout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        return convertToResponse(savedWorkout);
    }

    public List<WorkoutResponse> getUserWorkouts() {
        User user = getAuthenticatedUser();

        List<Workout> workouts = workoutRepository.findByUser_UserId(user.getUserId());
        return workouts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Reorder an exercise within a workout
    @Transactional
    public void reorderExercise(Long workoutId, Long workoutExerciseId, Integer newOrderIndex) {
        // Get authenticated user
        User user = getAuthenticatedUser();

        // Verify workout ownership
        Workout workout = workoutRepository.findByWorkoutIdAndUser_UserId(workoutId, user.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "Workout not found or you don't have permission to modify it"));

        // Fetch the exercise to be moved
        WorkoutExercise exerciseToMove = workoutExerciseRepository
                .findById(workoutExerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        // Validate it belongs to the right workout
        if (!exerciseToMove.getWorkout().getWorkoutId().equals(workoutId)) {
            throw new RuntimeException("Exercise does not belong to this workout");
        }

        Integer oldOrderIndex = exerciseToMove.getOrderIndex();

        // If pos is same, do nothing
        if (oldOrderIndex.equals(newOrderIndex)) {
            return;
        }

        // Fetch all exercises in this workout
        List<WorkoutExercise> allExercises = workoutExerciseRepository
                .findByWorkout_WorkoutIdOrderByOrderIndexAsc(workoutId);

        // If exercise moves down (from lower to higher index)
        if (oldOrderIndex < newOrderIndex) {
            for (WorkoutExercise exercise : allExercises) {
                if (exercise.getOrderIndex() > oldOrderIndex &&
                        exercise.getOrderIndex() <= newOrderIndex) {
                    exercise.setOrderIndex(exercise.getOrderIndex() - 1);
                }
            }
        }
        // If exercise moves up (from higher to lower index)
        else if (oldOrderIndex > newOrderIndex) {
            for (WorkoutExercise exercise : allExercises) {
                if (exercise.getOrderIndex() >= newOrderIndex &&
                        exercise.getOrderIndex() < oldOrderIndex) {
                    exercise.setOrderIndex(exercise.getOrderIndex() + 1);
                }
            }
        }

        // Set new pos for exercise to be moved
        exerciseToMove.setOrderIndex(newOrderIndex);

        // Save all changes
        workoutExerciseRepository.saveAll(allExercises);
    }

    //Delete workout method
    @Transactional
    public Workout deleteWorkout(Long id) {

        // validate workoutId
        validation.validateLong(id, "Workout ID", 1L, "Workout ID must be greater than 0");

        // get authenticated user
        User user = getAuthenticatedUser();

        // fetch workout and verify ownership of it
        Workout workout = workoutRepository
                .findByWorkoutIdAndUser_UserId(id, user.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "Workout not found or you don't have permission to delete it"));


        workoutRepository.delete(workout);


        return workout;
    }

    private void validateExerciseNotInWorkout(Workout workout, Long exerciseId) {
        boolean exerciseExists = workout.getWorkoutExercises().stream()
                .anyMatch(we -> exerciseId.equals(we.getExercise().getExerciseId()));

        if (exerciseExists) {
            throw new RuntimeException("Exercise with id " + exerciseId +
                    " already exists in this workout.");
        }
    }

    // Helper method to get the authenticated user
    public User getAuthenticatedUser() {
        // Get the currently authenticated user's ID from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // This gets the email from the JWT

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    // Converter method: Entity -> DTO
    private WorkoutResponse convertToResponse(Workout workout) {
        List<WorkoutExerciseResponse> exerciseResponses = workout.getWorkoutExercises().stream()
                .sorted(Comparator.comparing(WorkoutExercise::getOrderIndex))
                .map(we -> {
                    we.getSets().size();
                    return WorkoutExerciseResponse.convert(we);
                })
                .collect(Collectors.toList());

        return new WorkoutResponse(
                workout.getWorkoutId(),
                workout.getName(),
                workout.getUser().getUserId(),
                workout.getCreatedAt(),
                exerciseResponses.size(),
                exerciseResponses
        );
    }
}