package com.jerry.workoutapp.repository;

import com.jerry.workoutapp.entity.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {
    // Fetches all exercises belonging to a specific workout
    // Ordered by their orderIndex in ascending order
    List<WorkoutExercise> findByWorkout_WorkoutIdOrderByOrderIndexAsc(Long workoutId);

    // Fetches A specific exercise that belongs to a specific workout
    // (for validating that the exercise actually belongs to the workout we are trying to change)
    Optional<WorkoutExercise> findByWorkoutExerciseIdAndWorkout_WorkoutId(
            Long workoutExerciseId, Long workoutId);
}

