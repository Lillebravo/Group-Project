package com.jerry.workoutapp.service;

import com.jerry.workoutapp.dto.WorkoutExerciseResponse;
import com.jerry.workoutapp.dto.WorkoutExerciseSetResponse;
import com.jerry.workoutapp.entity.User;
import com.jerry.workoutapp.entity.WorkoutExercise;
import com.jerry.workoutapp.entity.WorkoutExerciseSet;
import com.jerry.workoutapp.repository.*;
import com.jerry.workoutapp.util.Validation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkoutExerciseService {
    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final WorkoutExerciseSetRepository workoutExerciseSetRepository;
    private final Validation validation;
    private final WorkoutService workoutService;

    public WorkoutExerciseService(WorkoutRepository workoutRepository,
                                  UserRepository userRepository,
                                  ExerciseRepository exerciseRepository,
                                  WorkoutExerciseRepository workoutExerciseRepository,
                                  WorkoutExerciseSetRepository workoutExerciseSetRepository,
                                  Validation validation, WorkoutService workoutService) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.workoutExerciseSetRepository = workoutExerciseSetRepository;
        this.validation = validation;
        this.workoutService = workoutService;
    }

    // UPDATED: Now we update one specific set
    @Transactional
    public WorkoutExerciseSetResponse updateSet(Long workoutExerciseId, Integer setNumber,
                                                Integer newReps, Double newWeight) {
        // Authenticate user
        User user = workoutService.getAuthenticatedUser();

        // Fetch WorkoutExercise
        WorkoutExercise workoutExercise = workoutExerciseRepository
                .findById(workoutExerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        // Verify ownership
        if (workoutExercise.getWorkout().getUser().getUserId() != user.getUserId()) {
            throw new RuntimeException("You don't have permission to modify this exercise");
        }

        // Find the specific set
        WorkoutExerciseSet set = workoutExercise.getSets().stream()
                .filter(s -> s.getSetNumber().equals(setNumber))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Set " + setNumber + " not found"));

        // Validate and update
        if (newReps != null) {
            validation.validateInteger(newReps, "Reps", true, 1, null, "Reps måste vara minst 1");
            set.setTargetReps(newReps);
        }

        if (newWeight != null) {
            if (newWeight < 0) {
                throw new RuntimeException("Weight cannot be negative");
            }
            set.setTargetWeight(newWeight);
        }

        WorkoutExerciseSet saved = workoutExerciseSetRepository.save(set);

        // Return DTO
        return new WorkoutExerciseSetResponse(
                saved.getId(),
                saved.getWorkoutExercise().getWorkoutExerciseId(),
                saved.getSetNumber(),
                saved.getTargetReps(),
                saved.getTargetWeight()
        );
    }

    @Transactional
    public WorkoutExerciseResponse updateRestTime(Long workoutExerciseId, Integer newRestTime) {
        User user = workoutService.getAuthenticatedUser();

        WorkoutExercise workoutExercise = workoutExerciseRepository
                .findById(workoutExerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        // Verify ownership
        if (workoutExercise.getWorkout().getUser().getUserId() != user.getUserId()) {
            throw new RuntimeException("You don't have permission to modify this exercise");
        }

        if (newRestTime != null) {
            validation.validateInteger(newRestTime, "Rest time", true, 0, null,
                    "Rest time must be 0 or greater");
            workoutExercise.setRestTime(newRestTime);
        }

        WorkoutExercise saved = workoutExerciseRepository.save(workoutExercise);

        return convertToResponse(saved);
    }

    //Delete exercise inside workouts by workoutExerciseId
    @Transactional
    public void deleteExerciseFromWorkout(Long workoutExerciseId) {
        // Authenticate user
        User user = workoutService.getAuthenticatedUser();

        // Fetch the exercise to delete
        WorkoutExercise exerciseToDelete = workoutExerciseRepository
                .findById(workoutExerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        // Validate that the exercise belongs to the user's workout
        if (exerciseToDelete.getWorkout().getUser().getUserId() != user.getUserId()) {
            throw new RuntimeException("You don't have permission to delete this exercise");
        }

        Long workoutId = exerciseToDelete.getWorkout().getWorkoutId();
        Integer deletedOrderIndex = exerciseToDelete.getOrderIndex();

        // Delete the exercise
        workoutExerciseRepository.delete(exerciseToDelete);
        workoutExerciseRepository.flush();

        // Update order_index for all exercises after the deleted one
        List<WorkoutExercise> remainingExercises = workoutExerciseRepository
                .findByWorkout_WorkoutIdOrderByOrderIndexAsc(workoutId);

        for (WorkoutExercise exercise : remainingExercises) {
            if (exercise.getOrderIndex() > deletedOrderIndex) {
                exercise.setOrderIndex(exercise.getOrderIndex() - 1);
            }
        }

        workoutExerciseRepository.saveAll(remainingExercises);
    }

    // Helper method to convertert to DTO
    private WorkoutExerciseResponse convertToResponse(WorkoutExercise we) {
        return new WorkoutExerciseResponse(
                we.getWorkoutExerciseId(),
                we.getExercise().getExerciseId(),
                we.getExercise().getName(),
                we.getExercise().getCategory(),
                we.getRestTime(),
                we.getSets().size(), // number of sets
                we.getOrderIndex()
        );
    }

}
