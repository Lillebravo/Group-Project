package com.jerry.workoutapp.repository;

import com.jerry.workoutapp.entity.RoutineWorkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoutineWorkoutRepository extends JpaRepository<RoutineWorkout, Long> {
    Optional<RoutineWorkout> findByRoutine_IdAndWorkout_WorkoutId(Long routineId, Long workoutId);
}
