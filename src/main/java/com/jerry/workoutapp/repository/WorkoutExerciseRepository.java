package com.jerry.workoutapp.repository;

import com.jerry.workoutapp.entity.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {
    // Fetches all exercises belonging to a specific workout
    // Ordered by their orderIndex in ascending order
    List<WorkoutExercise> findByWorkout_WorkoutIdOrderByOrderIndexAsc(Long workoutId);

    // Fetches A specific exercise that belongs to a specific workout
    // (for validating that the exercise actually belongs to the workout we are trying to change)
    Optional<WorkoutExercise> findByWorkoutExerciseIdAndWorkout_WorkoutId(
            Long workoutExerciseId, Long workoutId);

    Optional<WorkoutExercise> findByWorkout_WorkoutIdAndExercise_ExerciseIdAndWorkout_User_UserId(
            Long workoutId,
            Long exerciseId,
            Long userId
    );
}

