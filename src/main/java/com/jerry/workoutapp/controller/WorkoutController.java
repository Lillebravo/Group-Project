package com.jerry.workoutapp.controller;

import com.jerry.workoutapp.entity.Workout;
import com.jerry.workoutapp.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workouts")
public class WorkoutController {

    @Autowired
    private WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<Workout> createWorkout(@RequestBody WorkoutRequest request) {
        Workout workout = workoutService.createWorkout(request.getName());
        return ResponseEntity.ok(workout);
    }
}

class WorkoutRequest {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}