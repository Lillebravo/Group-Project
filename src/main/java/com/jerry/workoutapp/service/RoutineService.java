package com.jerry.workoutapp.service;

import com.jerry.workoutapp.dto.RoutineResponse;
import com.jerry.workoutapp.entity.Routine;
import com.jerry.workoutapp.entity.User;
import com.jerry.workoutapp.repository.RoutineRepository;
import com.jerry.workoutapp.service.WorkoutService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RoutineService {
    private final RoutineRepository routineRepository;
    private final WorkoutService workoutService;

    public RoutineService(RoutineRepository routineRepository, WorkoutService workoutService) {
        this.routineRepository = routineRepository;
        this.workoutService = workoutService;
    }

    @Transactional
    public RoutineResponse createRoutine(String routineName, String description) {
        User user = workoutService.getAuthenticatedUser();

        boolean exists = routineRepository.existsByNameAndUser_UserId(routineName, user.getUserId());
        if (exists) {
            throw new RuntimeException("Routine with name '" + routineName + "' already exists for this user.");
        }

        Routine routine = new Routine(user, routineName, description, LocalDateTime.now());
        Routine savedRoutine = routineRepository.save(routine);

        return new RoutineResponse(
                savedRoutine.getId(),
                savedRoutine.getName(),
                savedRoutine.getDescription(),
                savedRoutine.getCreatedAt(),
                null // ännu inga workouts
        );
    }
}
