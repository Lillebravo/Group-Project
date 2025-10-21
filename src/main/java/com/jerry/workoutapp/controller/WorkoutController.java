package com.jerry.workoutapp.controller;

import com.jerry.workoutapp.dto.AddExerciseRequest;
import com.jerry.workoutapp.dto.CreateWorkoutRequest;
import com.jerry.workoutapp.dto.ReorderExerciseDto;
import com.jerry.workoutapp.dto.WorkoutResponse;
import com.jerry.workoutapp.service.WorkoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    // Create a new workout for a user
    // POST /api/workouts
    // Request body contains workout name
    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(@RequestBody CreateWorkoutRequest request) {
        WorkoutResponse response = workoutService.createWorkout(request.getName());
        return ResponseEntity.ok(response);
    }

    // Get all workouts for a single user
    // GET /api/workouts/user
    @GetMapping("/user")
    public ResponseEntity<List<WorkoutResponse>> getUserWorkouts() {
        List<WorkoutResponse> workouts = workoutService.getUserWorkouts();
        return ResponseEntity.ok(workouts);
    }

    // Add an exercise to a specific workout by its ID
    // POST /api/workouts/1/exercises
    // Request body contains exerciseId, restTime, orderIndex, and sets array
    @PostMapping("/{workoutId}/exercises")
    public ResponseEntity<WorkoutResponse> addExerciseToWorkout(
            @PathVariable Long workoutId,
            @RequestBody AddExerciseRequest request) {
        WorkoutResponse updatedWorkout = workoutService.addExerciseToWorkout(
                workoutId,
                request.getExerciseId(),
                request.getRestTime(),
                request.getOrderIndex(),
                request.getSets()
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

    // Delete workout method
    // DELETE /api/workouts/{workoutId}
    @DeleteMapping("/{workoutId}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long workoutId){
        workoutService.deleteWorkout(workoutId);
        return ResponseEntity.ok().build();
    }
}