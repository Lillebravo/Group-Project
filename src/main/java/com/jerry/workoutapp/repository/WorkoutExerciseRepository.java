package com.jerry.workoutapp.repository;

import com.jerry.workoutapp.entity.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {
    // fetches all exercises belonging to a specific workout, ordered by their orderIndex in ascending order
    List<WorkoutExercise> findByWorkout_WorkoutIdOrderByOrderIndexAsc(Long workoutId);

    // Hämtar EN specifik övning som tillhör ett specifikt workout (för att validera att övningen faktiskt tillhör det workout vi försöker ändra)
    Optional<WorkoutExercise> findByWorkoutExerciseIdAndWorkout_WorkoutId(Long workoutExerciseId, Long workoutId);
}

