package com.jerry.workoutapp.controller;

import com.jerry.workoutapp.dto.UpdateWorkoutExerciseRequest;
import com.jerry.workoutapp.dto.WorkoutExerciseResponse;
import com.jerry.workoutapp.service.WorkoutExerciseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workoutExercises")
public class WorkoutExerciseController {

    private final WorkoutExerciseService workoutExerciseService;

    public WorkoutExerciseController(WorkoutExerciseService workoutExerciseService) {
        this.workoutExerciseService = workoutExerciseService;
    }

    // api/workoutExercises/{workoutId}/exercise/{exerciseId}
    // Update sets and reps for a specific exercise in a workout
    // Request body contains sets AND/OR reps
    @PutMapping("/{workoutId}/exercise/{exerciseId}")
    public ResponseEntity<WorkoutExerciseResponse> updateSetsAndReps(
            @PathVariable Long workoutId,
            @PathVariable Long exerciseId,
            @RequestBody UpdateWorkoutExerciseRequest request
    ) {
        WorkoutExerciseResponse response = workoutExerciseService.updateSetsAndReps(
                workoutId,
                exerciseId,
                request.getSets(),
                request.getReps()
        );

        return ResponseEntity.ok(response);
    }

}
