package com.jerry.workoutapp.repository;

import com.jerry.workoutapp.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Exercise, Long> {
}
