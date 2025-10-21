package com.jerry.workoutapp.repository;

import com.jerry.workoutapp.entity.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {
    List<WorkoutLog> findByUser_UserIdOrderByCompletedAtDesc(Long userId);
}
