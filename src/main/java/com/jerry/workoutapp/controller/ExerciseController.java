package com.jerry.workoutapp.controller;

import com.jerry.workoutapp.dto.AddCustomExercise;
import com.jerry.workoutapp.dto.ExerciseResponse;
import com.jerry.workoutapp.entity.User;
import com.jerry.workoutapp.repository.UserRepository;
import com.jerry.workoutapp.service.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final UserRepository userRepository;

    public ExerciseController(ExerciseService exerciseService, UserRepository userRepository) {
        this.exerciseService = exerciseService;
        this.userRepository = userRepository;
    }

    // GET /api/exercises/search?keyword=chest
    @GetMapping("/search")
    public ResponseEntity<List<ExerciseResponse>> searchExercises(
            @RequestParam String keyword,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<ExerciseResponse> exercises = exerciseService.searchExercises(keyword, userId);
        return ResponseEntity.ok(exercises);
    }

    // GET /api/exercises - Get all exercises (default + user's custom)
    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> getAllExercises(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<ExerciseResponse> exercises = exerciseService.getAllExercises(userId);
        return ResponseEntity.ok(exercises);
    }

    // POST /api/exercises/custom - Create custom exercise
    @PostMapping ("/custom")
    public ResponseEntity<?> createCustomExercise(
            @Valid @RequestBody AddCustomExercise request,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            ExerciseResponse exercise = exerciseService.createCustomExercise(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(exercise);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PUT /api/exercises/{id} - Update custom exercise
    @PutMapping("/custom/{id}")
    public ResponseEntity<?> updateCustomExercise(
            @PathVariable Long id,
            @Valid @RequestBody AddCustomExercise request,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            ExerciseResponse exercise = exerciseService.updateCustomExercise(id, request, userId);
            return ResponseEntity.ok(exercise);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /api/exercises/{id} - Delete custom exercise
    @DeleteMapping("/custom/{id}")
    public ResponseEntity<?> deleteCustomExercise(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            exerciseService.deleteCustomExercise(id, userId);
            return ResponseEntity.ok(Map.of("message", "Övning raderad"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Helper method to extract user ID from JWT token
    private Long getUserIdFromAuth(Authentication authentication) {
        // Get email from JWT token (stored in username field)
        String email = authentication.getName();

        // Look up user in database by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Användare hittades inte"));

        return user.getUserId();
    }
}