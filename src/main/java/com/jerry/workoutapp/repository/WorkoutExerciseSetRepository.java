package com.jerry.workoutapp.repository;

import com.jerry.workoutapp.entity.WorkoutExerciseSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutExerciseSetRepository extends JpaRepository<WorkoutExerciseSet, Long> {
    // Get all sets for a specific WorkoutExercise
    List<WorkoutExerciseSet> findByWorkoutExercise_WorkoutExerciseIdOrderBySetNumberAsc(
            Long workoutExerciseId);

    // Get a specific set
    Optional<WorkoutExerciseSet> findByWorkoutExercise_WorkoutExerciseIdAndSetNumber(
            Long workoutExerciseId, Integer setNumber);
}
