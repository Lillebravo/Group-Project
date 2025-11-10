package com.jerry.workoutapp.controller;

import com.jerry.workoutapp.dto.RoutineWorkoutRequest;
import com.jerry.workoutapp.service.RoutineService;
import com.jerry.workoutapp.service.RoutineWorkoutSerivce;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routineWorkouts")
public class RoutineWorkoutController {

    private final RoutineWorkoutSerivce routineWorkoutSerivce;
    private final RoutineService routineService;

    public RoutineWorkoutController(RoutineWorkoutSerivce routineWorkoutSerivce, RoutineService routineService) {
        this.routineWorkoutSerivce = routineWorkoutSerivce;
        this.routineService = routineService;
    }

    // Add exsisting workout to a routine
    // POST /{routineId}/workouts
    @PostMapping("/{routineId}/workouts")
    public ResponseEntity<String> addWorkoutToRoutine(
            @PathVariable Long routineId,
            @RequestBody RoutineWorkoutRequest request) {

        routineWorkoutSerivce.addWorkoutToRoutine(
                routineId,
                request.getWorkoutId(),
                request.getWeekDay(),
                request.getDayOrder()
        );

        return ResponseEntity.ok("Workout added to routine successfully.");
    }

    // Update day and order for a routine
    // PUT /{routineId}/workouts/{workoutId}
    @PutMapping("/{routineId}/workouts/{workoutId}")
    public ResponseEntity<String> updateWorkoutInRoutine(
            @PathVariable Long routineId,
            @PathVariable Long workoutId,
            @RequestBody RoutineWorkoutRequest request) {

        routineWorkoutSerivce.updateWorkoutOrder(
                routineId,
                workoutId,
                request.getDayOrder(),
                request.getWeekDay()
        );

        return ResponseEntity.ok("Workout updated successfully.");
    }

    // Delete a workout from a routine
    // DELETE /{routineId}/workouts/{workoutId}
    @DeleteMapping("/{routineId}/workouts/{workoutId}")
    public ResponseEntity<String> removeWorkoutFromRoutine(
            @PathVariable Long routineId,
            @PathVariable Long workoutId) {

        routineWorkoutSerivce.removeWorkoutFromRoutine(routineId, workoutId);
        return ResponseEntity.ok("Workout removed from routine successfully.");
    }
}
