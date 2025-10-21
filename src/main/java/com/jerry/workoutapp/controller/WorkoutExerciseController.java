package com.jerry.workoutapp.controller;

import com.jerry.workoutapp.dto.UpdateWorkoutExerciseSetRequest;
import com.jerry.workoutapp.dto.WorkoutExerciseResponse;
import com.jerry.workoutapp.dto.WorkoutExerciseSetResponse;
import com.jerry.workoutapp.service.WorkoutExerciseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/workoutExercises")
public class WorkoutExerciseController {

    private final WorkoutExerciseService workoutExerciseService;

    public WorkoutExerciseController(WorkoutExerciseService workoutExerciseService) {
        this.workoutExerciseService = workoutExerciseService;
    }

    // UPPDATED: Now we update a specific SET instead of the whole exercise
    // PUT /api/workoutExercises/{workoutExerciseId}/sets/{setNumber}
    // Request body contains targetReps AND/OR targetWeight
    @PutMapping("/{workoutExerciseId}/sets/{setNumber}")
    public ResponseEntity<WorkoutExerciseSetResponse> updateSet(
            @PathVariable Long workoutExerciseId,
            @PathVariable Integer setNumber,
            @RequestBody UpdateWorkoutExerciseSetRequest request
    ) {
        WorkoutExerciseSetResponse response = workoutExerciseService.updateSet(
                workoutExerciseId,
                setNumber,
                request.getTargetReps(),
                request.getTargetWeight()
        );

        return ResponseEntity.ok(response);
    }

    // NEW: Update rest_time for an exercise in a workout
    // PUT /api/workoutExercises/{workoutExerciseId}/restTime
    @PutMapping("/{workoutExerciseId}/restTime")
    public ResponseEntity<WorkoutExerciseResponse> updateRestTime(
            @PathVariable Long workoutExerciseId,
            @RequestBody Map<String, Integer> request
    ) {
        Integer restTime = request.get("restTime");
        WorkoutExerciseResponse response = workoutExerciseService.updateRestTime(
                workoutExerciseId,
                restTime
        );
        return ResponseEntity.ok(response);
    }

    // DELETE /api/workoutExercises/{workoutExerciseId}
    @DeleteMapping("/{workoutExerciseId}")
    public ResponseEntity<Map<String, String>> deleteExerciseFromWorkout(@PathVariable Long workoutExerciseId) {
        workoutExerciseService.deleteExerciseFromWorkout(workoutExerciseId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Exercise deleted");
        return ResponseEntity.ok(response);
    }

}
