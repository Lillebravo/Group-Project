package com.jerry.workoutapp.service;

import com.jerry.workoutapp.dto.WorkoutExerciseResponse;
import com.jerry.workoutapp.dto.WorkoutResponse;
import com.jerry.workoutapp.entity.Exercise;
import com.jerry.workoutapp.entity.User;
import com.jerry.workoutapp.entity.Workout;
import com.jerry.workoutapp.entity.WorkoutExercise;
import com.jerry.workoutapp.repository.ExerciseRepository;
import com.jerry.workoutapp.repository.UserRepository;
import com.jerry.workoutapp.repository.WorkoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutService(WorkoutRepository workoutRepository,
                          UserRepository userRepository,
                          ExerciseRepository exerciseRepository) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Transactional
    public WorkoutResponse createWorkout(Long userId, String workoutName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Workout workout = new Workout(workoutName, user);
        Workout savedWorkout = workoutRepository.save(workout);
        return convertToResponse(savedWorkout);
    }

    @Transactional
    public WorkoutResponse addExerciseToWorkout(Long workoutId, Long exerciseId, Integer sets, Integer reps, Integer orderIndex) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + exerciseId));

        workout.addExercise(exercise, sets, reps, orderIndex);
        Workout savedWorkout = workoutRepository.save(workout);
        return convertToResponse(savedWorkout);
    }

    public List<WorkoutResponse> getUserWorkouts(Long userId) {
        List<Workout> workouts = workoutRepository.findByUser_UserId(userId);
        return workouts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Converter method: Entity -> DTO
    private WorkoutResponse convertToResponse(Workout workout) {
        List<WorkoutExerciseResponse> exerciseResponses = workout.getWorkoutExercises().stream()
                .map(this::convertToExerciseResponse)
                .collect(Collectors.toList());

        return new WorkoutResponse(
                workout.getWorkoutId(),
                workout.getName(),
                workout.getUser().getUserId(),
                workout.getCreatedAt(),
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