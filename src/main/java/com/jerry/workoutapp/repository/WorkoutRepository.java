package com.jerry.workoutapp.repository;

import com.jerry.workoutapp.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByUser_UserId(Long userId);

    Optional<Workout> findByNameAndUser_UserId(String name, Long userId);

    Optional<Workout> findByWorkoutIdAndUser_UserId(Long workoutId, Long userId);
}