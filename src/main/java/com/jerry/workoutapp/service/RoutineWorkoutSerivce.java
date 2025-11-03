package com.jerry.workoutapp.service;

import com.jerry.workoutapp.dto.RoutineResponse;
import com.jerry.workoutapp.entity.Routine;
import com.jerry.workoutapp.entity.RoutineWorkout;
import com.jerry.workoutapp.entity.User;
import com.jerry.workoutapp.entity.Workout;
import com.jerry.workoutapp.repository.RoutineRepository;
import com.jerry.workoutapp.repository.RoutineWorkoutRepository;
import com.jerry.workoutapp.repository.WorkoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
public class RoutineWorkoutSerivce {
private final WorkoutService workoutService;
private final RoutineRepository routineRepository;
private final WorkoutRepository workoutRepository;
private final RoutineWorkoutRepository routineWorkoutRepository;

    public RoutineWorkoutSerivce(WorkoutService workoutService, RoutineRepository routineRepository, WorkoutRepository workoutRepository, RoutineWorkoutRepository routineWorkoutRepository) {
        this.workoutService = workoutService;
        this.routineRepository = routineRepository;
        this.workoutRepository = workoutRepository;
        this.routineWorkoutRepository = routineWorkoutRepository;
    }


    @Transactional
    public void addWorkoutToRoutine(Long routineId, Long workoutId, String weekDay, Integer dayOrder) {
        // 1️⃣ Hämta inloggad användare
        User user = workoutService.getAuthenticatedUser();

        // 2️⃣ Hämta rutinen och säkerställ att den tillhör användaren
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Routine not found"));
        if (routine.getUser().getUserId() != user.getUserId()) {
            throw new RuntimeException("You don't have permission to modify this routine.");
        }

        // 3️⃣ Hämta workout och säkerställ att den tillhör användaren
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found"));
        if (workout.getUser().getUserId() != user.getUserId()) {
            throw new RuntimeException("You don't have permission to add this workout.");
        }


        // 4️⃣ Skapa kopplingen i routine_workouts
        RoutineWorkout rw = new RoutineWorkout();
        rw.setRoutine(routine);
        rw.setWorkout(workout);
        rw.setWeekDay(weekDay);
        rw.setDayOrder(dayOrder);

        routineWorkoutRepository.save(rw);
    }

    @Transactional
    public void removeWorkoutFromRoutine(Long routineId, Long workoutId) {
        User user = workoutService.getAuthenticatedUser();

        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Routine not found"));
        if (routine.getUser().getUserId() != user.getUserId()) {
            throw new RuntimeException("You don't have permission to modify this routine.");
        }

        RoutineWorkout rw = routineWorkoutRepository.findByRoutine_IdAndWorkout_WorkoutId(routineId, workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found in this routine."));

        routineWorkoutRepository.delete(rw);
    }

    @Transactional
    public void updateWorkoutOrder(Long routineId, Long workoutId, Integer newDayOrder, String newWeekDay) {
        User user = workoutService.getAuthenticatedUser();

        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Routine not found"));
        if (routine.getUser().getUserId() != user.getUserId()) {
            throw new RuntimeException("You don't have permission to modify this routine.");
        }

        RoutineWorkout rw = routineWorkoutRepository.findByRoutine_IdAndWorkout_WorkoutId(routineId, workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found in this routine."));

        rw.setDayOrder(newDayOrder);
        rw.setWeekDay(newWeekDay);
        routineWorkoutRepository.save(rw);
    }
}

