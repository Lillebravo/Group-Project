package com.jerry.workoutapp.dto;

public class ExerciseResponse {

    private String name;

    private String description;

    private String muscleGroup;

    private String equipment;

    public ExerciseResponse(String name, String muscleGroup, String description, String equipment) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.description = description;
        this.equipment = equipment;
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public String getMuscleGroup() {return muscleGroup;}
    public void setMuscleGroup(String muscleGroup) {this.muscleGroup = muscleGroup;}

    public String getEquipment() {return equipment;}
    public void setEquipment(String equipment) {this.equipment = equipment;}
}
