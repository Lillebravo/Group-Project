package com.jerry.workoutapp.repository;

import com.jerry.workoutapp.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;




@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {


    boolean existsByNameAndUser_UserId(String routineName, long userId);


}

