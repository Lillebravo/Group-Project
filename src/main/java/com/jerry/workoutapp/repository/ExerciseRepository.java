package com.jerry.workoutapp.repository;

import com.jerry.workoutapp.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            String name, String category);
}
