package com.jerry.workoutapp.controller;

import com.jerry.workoutapp.dto.CreateRoutineRequest;
import com.jerry.workoutapp.dto.RoutineResponse;
import com.jerry.workoutapp.dto.RoutineWorkoutRequest;
import com.jerry.workoutapp.repository.RoutineWorkoutRepository;
import com.jerry.workoutapp.service.RoutineService;
import com.jerry.workoutapp.service.RoutineWorkoutSerivce;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routineWorkouts")
public class RoutineWorkoutController {

    private final RoutineWorkoutRepository routineWorkoutRepository;
    private final RoutineWorkoutSerivce routineWorkoutSerivce;
    private final RoutineService routineService;

    public RoutineWorkoutController(RoutineWorkoutRepository routineWorkoutRepository, RoutineWorkoutSerivce routineWorkoutSerivce, RoutineService routineService) {
        this.routineWorkoutRepository = routineWorkoutRepository;
        this.routineWorkoutSerivce = routineWorkoutSerivce;
        this.routineService = routineService;
    }
    // 🟢 1️⃣ Skapa ny rutin (med namn, beskrivning och workouts)
    @PostMapping
    public ResponseEntity<RoutineResponse> createRoutine(@RequestBody CreateRoutineRequest request) {
        // Steg 1: skapa själva rutinen
        RoutineResponse routineResponse = routineService.createRoutine(
                request.getName(),
                request.getDescription()
        );

        // Steg 2: lägg till workouts i rutinen om några finns
        if (request.getWorkouts() != null && !request.getWorkouts().isEmpty()) {
            for (CreateRoutineRequest.RoutineWorkoutDto workoutDto : request.getWorkouts()) {
                routineWorkoutSerivce.addWorkoutToRoutine(
                        routineResponse.getId(),
                        workoutDto.getWorkoutId(),
                        workoutDto.getWeekDay(),
                        workoutDto.getDayOrder()
                );
            }
        }

        return ResponseEntity.ok(routineResponse);
    }

    // 🟣 2️⃣ Lägg till ett nytt workout-pass i en befintlig rutin
    @PostMapping("/{routineId}/workouts")
    public ResponseEntity<String> addWorkoutToRoutine(
            @PathVariable Long routineId,
            @RequestBody CreateRoutineRequest.RoutineWorkoutDto request) {

        routineWorkoutSerivce.addWorkoutToRoutine(
                routineId,
                request.getWorkoutId(),
                request.getWeekDay(),
                request.getDayOrder()
        );

        return ResponseEntity.ok("Workout added to routine successfully.");
    }

    // 🟡 3️⃣ Uppdatera dag och ordning för ett workout-pass
    @PutMapping("/{routineId}/workouts/{workoutId}")
    public ResponseEntity<String> updateWorkoutInRoutine(
            @PathVariable Long routineId,
            @PathVariable Long workoutId,
            @RequestBody CreateRoutineRequest.RoutineWorkoutDto request) {

        routineWorkoutSerivce.updateWorkoutOrder(
                routineId,
                workoutId,
                request.getDayOrder(),
                request.getWeekDay()
        );

        return ResponseEntity.ok("Workout updated successfully.");
    }

    // 🔴 4️⃣ Ta bort ett workout-pass från en rutin
    @DeleteMapping("/{routineId}/workouts/{workoutId}")
    public ResponseEntity<String> removeWorkoutFromRoutine(
            @PathVariable Long routineId,
            @PathVariable Long workoutId) {

        routineWorkoutSerivce.removeWorkoutFromRoutine(routineId, workoutId);
        return ResponseEntity.ok("Workout removed from routine successfully.");
    }
}
