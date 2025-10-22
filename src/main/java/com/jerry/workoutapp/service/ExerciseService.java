package com.jerry.workoutapp.service;

import com.jerry.workoutapp.dto.AddCustomExercise;
import com.jerry.workoutapp.dto.ExerciseResponse;
import com.jerry.workoutapp.entity.Exercise;
import com.jerry.workoutapp.entity.User;
import com.jerry.workoutapp.entity.UserExercisePreference;
import com.jerry.workoutapp.repository.ExerciseRepository;
import com.jerry.workoutapp.repository.UserExercisePreferenceRepository;
import com.jerry.workoutapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final UserExercisePreferenceRepository userExercisePreferenceRepository;
    private final UserRepository userRepository;

    public ExerciseService(ExerciseRepository exerciseRepository,
                           UserExercisePreferenceRepository userExercisePreferenceRepository,
                           UserRepository userRepository) {
        this.exerciseRepository = exerciseRepository;
        this.userExercisePreferenceRepository = userExercisePreferenceRepository;
        this.userRepository = userRepository;
    }

    public List<ExerciseResponse> searchExercises(String keyword, Long userId) {
        // Get all exercises matching keyword
        List<Exercise> allExercises = exerciseRepository
                .findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(keyword, keyword);

        // Get user's custom exercise IDs
        List<Long> customExerciseIds = userExercisePreferenceRepository
                .findByUser_UserIdAndIsCustom(userId, 1)
                .stream()
                .map(pref -> pref.getExercise().getExerciseId())
                .collect(Collectors.toList());

        // Get ALL custom exercise IDs (from any user) - ONE query
        List<Long> allCustomExerciseIds = userExercisePreferenceRepository
                .findByIsCustom(1)
                .stream()
                .map(pref -> pref.getExercise().getExerciseId())
                .toList();

        // Filter: show default exercises + user's custom exercises
        return allExercises.stream()
                .filter(ex -> !allCustomExerciseIds.contains(ex.getExerciseId())
                        || customExerciseIds.contains(ex.getExerciseId()))
                .map(ex -> new ExerciseResponse(
                        ex.getExerciseId(),
                        ex.getName(),
                        ex.getDescription(),
                        ex.getCategory()
                ))
                .collect(Collectors.toList());
    }

    public List<ExerciseResponse> getAllExercises(Long userId) {
        List<Exercise> allExercises = exerciseRepository.findAll();

        List<Long> userCustomIds = userExercisePreferenceRepository
                .findByUser_UserIdAndIsCustom(userId, 1)
                .stream()
                .map(pref -> pref.getExercise().getExerciseId())
                .toList();

        // Get ALL custom exercise IDs (from any user) - ONE query
        List<Long> allCustomExerciseIds = userExercisePreferenceRepository
                .findByIsCustom(1)
                .stream()
                .map(pref -> pref.getExercise().getExerciseId())
                .toList();

        return allExercises.stream()
                .filter(ex -> !allCustomExerciseIds.contains(ex.getExerciseId())
                        || userCustomIds.contains(ex.getExerciseId()))
                .map(ex -> new ExerciseResponse(
                        ex.getExerciseId(),
                        ex.getName(),
                        ex.getDescription(),
                        ex.getCategory()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ExerciseResponse createCustomExercise(AddCustomExercise request, Long userId) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Övningsnamn är obligatoriskt");
        }
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Muskelgrupp är obligatorisk");
        }

        // Get user entity
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Användare hittades inte"));

        // Create exercise in exercises table
        Exercise exercise = new Exercise();
        exercise.setName(request.getName().trim());
        exercise.setCategory(request.getCategory().trim());
        exercise.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        exercise.setCreatedAt(LocalDateTime.now());

        Exercise savedExercise = exerciseRepository.save(exercise);

        // Create preference entry to mark as custom
        UserExercisePreference preference = new UserExercisePreference();
        preference.setUser(user);
        preference.setExercise(savedExercise);
        preference.setIsCustom(); // Sets to 1
        preference.setCreatedAt(LocalDateTime.now());

        userExercisePreferenceRepository.save(preference);

        return new ExerciseResponse(
                savedExercise.getExerciseId(),
                savedExercise.getName(),
                savedExercise.getDescription(),
                savedExercise.getCategory()
        );
    }

    @Transactional
    public ExerciseResponse updateCustomExercise(Long exerciseId, AddCustomExercise request, Long userId) {

        // Check if user owns this custom exercise
        boolean owns = userExercisePreferenceRepository.existsByUser_UserIdAndExercise_ExerciseIdAndIsCustom(userId, exerciseId, 1);
        System.out.println("User owns exercise: " + owns);
        if (!owns) {
            throw new IllegalArgumentException("Du kan bara redigera dina egna övningar");
        }

        // Validate input
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Övningsnamn är obligatoriskt");
        }
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Muskelgrupp är obligatorisk");
        }

        // Get and update exercise
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Övning hittades inte"));

        exercise.setName(request.getName().trim());
        exercise.setCategory(request.getCategory().trim());
        exercise.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");

        Exercise updatedExercise = exerciseRepository.save(exercise);

        return new ExerciseResponse(
                updatedExercise.getExerciseId(),
                updatedExercise.getName(),
                updatedExercise.getDescription(),
                updatedExercise.getCategory()
        );
    }

    @Transactional
    public void deleteCustomExercise(Long exerciseId, Long userId) {
        // Check if user owns this custom exercise
        if (!userExercisePreferenceRepository.existsByUser_UserIdAndExercise_ExerciseIdAndIsCustom(userId, exerciseId, 1)) {
            throw new IllegalArgumentException("Du kan bara radera dina egna övningar");
        }

        //Remove from both tables
        userExercisePreferenceRepository.deleteByUser_UserIdAndExercise_ExerciseId(userId, exerciseId);
        exerciseRepository.deleteById(exerciseId);
    }
}