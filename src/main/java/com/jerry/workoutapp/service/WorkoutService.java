package com.jerry.workoutapp.service;

import com.jerry.workoutapp.dto.WorkoutExerciseResponse;
import com.jerry.workoutapp.dto.WorkoutResponse;
import com.jerry.workoutapp.entity.Exercise;
import com.jerry.workoutapp.entity.User;
import com.jerry.workoutapp.entity.Workout;
import com.jerry.workoutapp.entity.WorkoutExercise;
import com.jerry.workoutapp.repository.ExerciseRepository;
import com.jerry.workoutapp.repository.UserRepository;
import com.jerry.workoutapp.repository.WorkoutExerciseRepository;
import com.jerry.workoutapp.repository.WorkoutRepository;
import com.jerry.workoutapp.util.Validation;
import jakarta.persistence.EntityNotFoundException;
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
    private final Validation validation;

    public WorkoutService(WorkoutRepository workoutRepository,
                          UserRepository userRepository,
                          ExerciseRepository exerciseRepository,
                          WorkoutExerciseRepository workoutExerciseRepository, Validation validation) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
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
    public WorkoutResponse addExerciseToWorkout(Long workoutId, Long exerciseId
            , Integer sets, Integer reps, Integer orderIndex) {

        User user = getAuthenticatedUser();

        // Fetch workout and verify ownership of it
        Workout workout = workoutRepository.findByWorkoutIdAndUser_UserId(workoutId, user.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "Workout not found or you don't have permission to modify it"));

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found with id: "
                        + exerciseId));

        List<WorkoutExercise> existingExercises = workoutExerciseRepository
                .findByWorkout_WorkoutIdOrderByOrderIndexAsc(workoutId);

        validateExerciseNotInWorkout(workout, exerciseId);

        // Om orderIndex inte anges (null), sätt den till sista positionen
        if (orderIndex == null) {
            if (existingExercises.isEmpty()) {
                orderIndex = 0;
            } else {
                // Hitta högsta order_index och lägg till 1
                orderIndex = existingExercises.stream()
                        .map(WorkoutExercise::getOrderIndex)
                        .max(Integer::compareTo)
                        .orElse(-1) + 1;
            }
        } else {
            // Om orderIndex är angiven och redan upptagen, flytta alla övningar nedåt
            for (WorkoutExercise we : existingExercises) {
                if (we.getOrderIndex() >= orderIndex) {
                    we.setOrderIndex(we.getOrderIndex() + 1);
                }
            }
            workoutExerciseRepository.saveAll(existingExercises);
            workoutExerciseRepository.flush();
        }

        workout.addExercise(exercise, sets, reps, orderIndex);
        Workout savedWorkout = workoutRepository.save(workout);
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

        //Validate workout id
        validation.validateLong(id, "Workout ID", 1L, "Workout ID must be greater than 0");

        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workout not found with id " + id));

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
    private User getAuthenticatedUser() {
        // Get the currently authenticated user's ID from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // This gets the email from the JWT

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    // Converter method: Entity -> DTO
    private WorkoutResponse convertToResponse(Workout workout) {
        List<WorkoutExerciseResponse> exerciseResponses = workout.getWorkoutExercises().stream()
                // Extra safety for making sure exercises are sorted by index
                .sorted(Comparator.comparing(WorkoutExercise::getOrderIndex))
                .map(this::convertToExerciseResponse)
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

    private WorkoutExerciseResponse convertToExerciseResponse(WorkoutExercise we) {
        Exercise exercise = we.getExercise();
        return new WorkoutExerciseResponse(
                we.getWorkoutExerciseId(),
                exercise.getExerciseId(),
                exercise.getName(),
                exercise.getMuscleGroup(),
                we.getSets(),
                we.getReps(),
                we.getOrderIndex()
        );
    }

}