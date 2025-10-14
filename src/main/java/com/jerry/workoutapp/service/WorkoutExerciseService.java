package com.jerry.workoutapp.service;

import com.jerry.workoutapp.dto.WorkoutExerciseResponse;
import com.jerry.workoutapp.entity.User;
import com.jerry.workoutapp.entity.Workout;
import com.jerry.workoutapp.entity.WorkoutExercise;
import com.jerry.workoutapp.repository.ExerciseRepository;
import com.jerry.workoutapp.repository.UserRepository;
import com.jerry.workoutapp.repository.WorkoutExerciseRepository;
import com.jerry.workoutapp.repository.WorkoutRepository;
import com.jerry.workoutapp.util.Validation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkoutExerciseService {
    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final Validation validation;
    private final WorkoutService workoutService;



    public WorkoutExerciseService(WorkoutRepository workoutRepository, UserRepository userRepository, ExerciseRepository exerciseRepository, WorkoutExerciseRepository workoutExerciseRepository, Validation validation, WorkoutService workoutService) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.validation = validation;
        this.workoutService = workoutService;
    }

    // Update sets and reps for a specific exercise in a workout
    @Transactional
    public WorkoutExerciseResponse updateSetsAndReps(Long workoutId, Long exerciseId, Integer newSets, Integer newReps) {

        // Authenticate user
        User user = workoutService.getAuthenticatedUser();

        WorkoutExercise workoutExercise = workoutExerciseRepository
                .findByWorkout_WorkoutIdAndExercise_ExerciseIdAndWorkout_User_UserId(workoutId, exerciseId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Exercise not found in this workout"));

        // Validate new sets and reps
        if (newSets != null) {
            validation.validateInteger(newSets, "Sets", true, 1, null, "Sets måste vara minst 1");
            workoutExercise.setSets(newSets);
        }

        if (newReps != null) {
            validation.validateInteger(newReps, "Reps", true, 1, null, "Reps måste vara minst 1");
            workoutExercise.setReps(newReps);
        }

        WorkoutExercise saved = workoutExerciseRepository.save(workoutExercise);

        // Return DTO with updated values
        return new WorkoutExerciseResponse(
                saved.getWorkoutExerciseId(),
                saved.getExercise().getExerciseId(),
                saved.getExercise().getName(),
                saved.getExercise().getMuscleGroup(),
                saved.getSets(),
                saved.getReps(),
                saved.getOrderIndex()
        );
    }

}
