package com.jerry.workoutapp.controller;

import com.jerry.workoutapp.dto.AddExerciseRequest;
import com.jerry.workoutapp.dto.CreateWorkoutRequest;
import com.jerry.workoutapp.dto.ReorderExerciseDto;
import com.jerry.workoutapp.dto.WorkoutResponse;
import com.jerry.workoutapp.service.WorkoutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    // Create a new workout for a user
    // POST /api/workouts
    // Request body contains userId and workout name
    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(@RequestBody CreateWorkoutRequest request) {
        WorkoutResponse createdWorkout = workoutService.createWorkout(request.getUserId(), request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWorkout);
    }

    // Get all workouts for a single user
    // GET /api/workouts?userId=1
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WorkoutResponse>> getUserWorkouts(@PathVariable Long userId) {
        List<WorkoutResponse> workouts = workoutService.getUserWorkouts(userId);
        return ResponseEntity.ok(workouts);
    }

    // Add an exercise to a specific workout by its ID
    // POST /api/workouts/1/exercises
    // Request body contains exerciseId, sets, reps, and orderIndex.
    @PostMapping("/{workoutId}/exercises")
    public ResponseEntity<WorkoutResponse> addExerciseToWorkout(
            @PathVariable Long workoutId,
            @RequestBody AddExerciseRequest request) {
        WorkoutResponse updatedWorkout = workoutService.addExerciseToWorkout(
                workoutId,
                request.getExerciseId(),
                request.getSets(),
                request.getReps(),
                request.getOrderIndex()
        );
        return ResponseEntity.ok(updatedWorkout);
    }

    // Reorder an exercise in a workout
    @PutMapping("/{workoutId}/exercises/reorder")
    public ResponseEntity<Void> reorderExercise(
            @PathVariable Long workoutId,
            @RequestBody ReorderExerciseDto request) {
        workoutService.reorderExercise(
                workoutId,
                request.getWorkoutExerciseId(),
                request.getNewOrderIndex()
        );
        return ResponseEntity.ok().build();
    }

}