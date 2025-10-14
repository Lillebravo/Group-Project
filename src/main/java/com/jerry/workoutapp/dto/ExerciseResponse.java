package com.jerry.workoutapp.dto;

public class ExerciseResponse {
    private Long exerciseId;

    private String name;

    private String description;

    private String muscleGroup;

    private String equipment;

    public ExerciseResponse(Long exerciseId, String name, String description, String muscleGroup, String equipment) {
        this.exerciseId = exerciseId;
        this.name = name;
        this.description = description;
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
    }

    public Long getExerciseId() {return exerciseId;}
    public void setExerciseId(Long exerciseId) {this.exerciseId = exerciseId;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public String getMuscleGroup() {return muscleGroup;}
    public void setMuscleGroup(String muscleGroup) {this.muscleGroup = muscleGroup;}

    public String getEquipment() {return equipment;}
    public void setEquipment(String equipment) {this.equipment = equipment;}
}
