package com.jerry.workoutapp.repository;

import com.jerry.workoutapp.entity.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {
    // fetches all exercises belonging to a specific workout, ordered by their orderIndex in ascending order
    List<WorkoutExercise> findByWorkout_WorkoutIdOrderByOrderIndexAsc(Long workoutId);
}