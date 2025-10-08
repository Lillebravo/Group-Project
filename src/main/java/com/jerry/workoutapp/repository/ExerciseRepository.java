package com.jerry.workoutapp.repository;

import com.jerry.workoutapp.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByNameContainingIgnoreCaseOrMuscleGroupContainingIgnoreCase(String name, String muscleGroup);
}
