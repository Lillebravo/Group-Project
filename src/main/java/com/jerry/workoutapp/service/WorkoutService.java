package com.jerry.workoutapp.service;

import com.jerry.workoutapp.dto.WorkoutExerciseResponse;
import com.jerry.workoutapp.dto.WorkoutResponse;
import com.jerry.workoutapp.entity.Exercise;
import com.jerry.workoutapp.entity.User;
import com.jerry.workoutapp.entity.Workout;
import com.jerry.workoutapp.entity.WorkoutExercise;
import com.jerry.workoutapp.repository.ExerciseRepository;
import com.jerry.workoutapp.repository.UserRepository;
import com.jerry.workoutapp.repository.WorkoutExerciseRepository;
import com.jerry.workoutapp.repository.WorkoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;

    public WorkoutService(WorkoutRepository workoutRepository,
                          UserRepository userRepository,
                          ExerciseRepository exerciseRepository,
                          WorkoutExerciseRepository workoutExerciseRepository) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
    }

    @Transactional
    public WorkoutResponse createWorkout(Long userId, String workoutName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Workout workout = new Workout(workoutName, user);
        Workout savedWorkout = workoutRepository.save(workout);
        return convertToResponse(savedWorkout);
    }

    @Transactional
    public WorkoutResponse addExerciseToWorkout(Long workoutId, Long exerciseId, Integer sets, Integer reps, Integer orderIndex) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + exerciseId));

        List<WorkoutExercise> existingExercises = workoutExerciseRepository
                .findByWorkout_WorkoutIdOrderByOrderIndexAsc(workoutId);

        // Om orderIndex inte anges (null), sätt den till sista positionen
        if (orderIndex == null) {
            if (existingExercises.isEmpty()) {
                orderIndex = 0;
            } else {
                // Hitta högsta order_index och lägg till 1
                orderIndex = existingExercises.stream()
                        .map(WorkoutExercise::getOrderIndex)
                        .max(Integer::compareTo)
                        .orElse(-1) + 1;
            }
        } else {
            // Om orderIndex är angiven och redan upptagen, flytta alla övningar nedåt
            for (WorkoutExercise we : existingExercises) {
                if (we.getOrderIndex() >= orderIndex) {
                    we.setOrderIndex(we.getOrderIndex() + 1);
                }
            }
            workoutExerciseRepository.saveAll(existingExercises);
            workoutExerciseRepository.flush();
        }

        workout.addExercise(exercise, sets, reps, orderIndex);
        Workout savedWorkout = workoutRepository.save(workout);
        return convertToResponse(savedWorkout);
    }

    public List<WorkoutResponse> getUserWorkouts(Long userId) {
        List<Workout> workouts = workoutRepository.findByUser_UserId(userId);
        return workouts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private void validateExerciseNotInWorkout(Workout workout, Long exerciseId) {
        boolean exerciseExists = workout.getWorkoutExercises().stream()
                .anyMatch(we -> exerciseId.equals(we.getExercise().getExerciseId()));

        if (exerciseExists) {
            throw new RuntimeException("Exercise with id " + exerciseId +
                    " already exists in this workout.");
        }
    }

    private void validateUniqueOrderIndex(Workout workout, Integer orderIndex) {
        boolean orderIndexExists = workout.getWorkoutExercises().stream()
                .anyMatch(we -> we.getOrderIndex().equals(orderIndex));

        if (orderIndexExists) {
            throw new RuntimeException("An exercise with order_index " + orderIndex +
                    " already exists in this workout. Please choose a different order_index.");
        }
    }

    // Converter method: Entity -> DTO
    private WorkoutResponse convertToResponse(Workout workout) {
        List<WorkoutExerciseResponse> exerciseResponses = workout.getWorkoutExercises().stream()
                .map(this::convertToExerciseResponse)
                .collect(Collectors.toList());

        return new WorkoutResponse(
                workout.getWorkoutId(),
                workout.getName(),
                workout.getUser().getUserId(),
                workout.getCreatedAt(),
                exerciseResponses.size(),
                exerciseResponses
        );
    }

    private WorkoutExerciseResponse convertToExerciseResponse(WorkoutExercise we) {
        Exercise exercise = we.getExercise();
        return new WorkoutExerciseResponse(
                we.getWorkoutExerciseId(),
                exercise.getExerciseId(),
                exercise.getName(),
                exercise.getMuscleGroup(),
                we.getSets(),
                we.getReps(),
                we.getOrderIndex()
        );
    }

    // Reorder an exercise within a workout
    @Transactional
    public void reorderExercise(Long workoutId, Long workoutExerciseId, Integer newOrderIndex) {
        // Hämta övningen som ska flyttas
        WorkoutExercise exerciseToMove = workoutExerciseRepository
                .findById(workoutExerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        // Validera att den tillhör rätt workout
        if (!exerciseToMove.getWorkout().getWorkoutId().equals(workoutId)) {
            throw new RuntimeException("Exercise does not belong to this workout");
        }

        Integer oldOrderIndex = exerciseToMove.getOrderIndex();

        // Om positionen är densamma, gör ingenting
        if (oldOrderIndex.equals(newOrderIndex)) {
            return;
        }

        // Hämta alla övningar för detta workout
        List<WorkoutExercise> allExercises = workoutExerciseRepository
                .findByWorkout_WorkoutIdOrderByOrderIndexAsc(workoutId);

        // Om övningen flyttas nedåt (från lägre till högre index)
        if (oldOrderIndex < newOrderIndex) {
            for (WorkoutExercise exercise : allExercises) {
                if (exercise.getOrderIndex() > oldOrderIndex &&
                        exercise.getOrderIndex() <= newOrderIndex) {
                    exercise.setOrderIndex(exercise.getOrderIndex() - 1);
                }
            }
        }
        // Om övningen flyttas uppåt (från högre till lägre index)
        else if (oldOrderIndex > newOrderIndex) {
            for (WorkoutExercise exercise : allExercises) {
                if (exercise.getOrderIndex() >= newOrderIndex &&
                        exercise.getOrderIndex() < oldOrderIndex) {
                    exercise.setOrderIndex(exercise.getOrderIndex() + 1);
                }
            }
        }

        // Sätt den nya positionen för övningen som flyttas
        exerciseToMove.setOrderIndex(newOrderIndex);

        // Spara alla ändringar
        workoutExerciseRepository.saveAll(allExercises);
    }

}