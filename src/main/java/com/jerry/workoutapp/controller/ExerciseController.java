package com.jerry.workoutapp.controller;

import com.jerry.workoutapp.entity.Exercise;
import com.jerry.workoutapp.service.ExerciseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    // GET /api/exercises/search?keyword=chest
    @GetMapping("/search")
    public ResponseEntity<List<Exercise>> searchExercises(@RequestParam String keyword) {
        List<Exercise> results = exerciseService.searchExercises(keyword);
        return ResponseEntity.ok(results);
    }
}
