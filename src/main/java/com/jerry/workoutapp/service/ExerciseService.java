package com.jerry.workoutapp.service;

import com.jerry.workoutapp.dto.ExerciseResponse;
import com.jerry.workoutapp.entity.Exercise;
import com.jerry.workoutapp.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public List<ExerciseResponse> searchExercises(String keyword) {
        List<Exercise> exercises = exerciseRepository
                .findByNameContainingIgnoreCaseOrMuscleGroupContainingIgnoreCase(keyword, keyword);

        return exercises.stream()
                .map(ex -> new ExerciseResponse(
                        ex.getName(),
                        ex.getDescription(),
                        ex.getMuscleGroup(),
                        ex.getEquipment()
                ))
                .collect(Collectors.toList());
    }

    public List<ExerciseResponse> getAllExercises(){
        List<Exercise> exercises = exerciseRepository.findAll();

        return exercises.stream()
                .map(ex -> new ExerciseResponse(
                        ex.getName(),
                        ex.getDescription(),
                        ex.getMuscleGroup(),
                        ex.getEquipment()
                ))
                .collect(Collectors.toList());
    }
}
