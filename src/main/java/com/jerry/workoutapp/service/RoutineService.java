package com.jerry.workoutapp.service;

import com.jerry.workoutapp.dto.RoutineResponse;
import com.jerry.workoutapp.entity.Routine;
import com.jerry.workoutapp.entity.User;
import com.jerry.workoutapp.entity.Workout;
import com.jerry.workoutapp.repository.*;
import com.jerry.workoutapp.util.Validation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoutineService {
    private final RoutineRepository routineRepository;
    private final WorkoutRepository workoutRepository;
    private final WorkoutService workoutService;

    public RoutineService(RoutineRepository routineRepository, WorkoutRepository workoutRepository, WorkoutService workoutService) {
        this.routineRepository = routineRepository;
        this.workoutRepository = workoutRepository;
        this.workoutService = workoutService;
    }

    @Transactional
    public RoutineResponse createRoutine(String routineName, String description){
        User user = workoutService.getAuthenticatedUser();

        boolean exists = routineRepository.existsByNameAndUser_UserId(routineName, user.getUserId());
        if(exists){
            throw new RuntimeException("Routine with name '" + routineName
                    + "' already exists for this user.");
        }

        Routine routine = new Routine(user, routineName, description, LocalDateTime.now());
        Routine savedRoutine = routineRepository.save(routine);
        return convertToResponse(savedRoutine);
    }



    private RoutineResponse convertToResponse(Routine routine) {
        return new RoutineResponse(
                routine.getId(),
                routine.getName(),
                routine.getDescription(),
                routine.getCreatedAt(),
                new ArrayList<>()  // Tom lista för workouts eftersom vi bara skapar rutinen
        );
    }



}
