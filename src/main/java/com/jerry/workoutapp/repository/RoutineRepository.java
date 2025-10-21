package com.jerry.workoutapp.repository;

import com.jerry.workoutapp.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine> findByUser_UserId(Long userId);
}
