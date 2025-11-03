package com.jerry.workoutapp.dto;

public class ExerciseResponse {

    private Long exerciseId;
    private String name;
    private String description;
    private String category;
    private Boolean isCustomExercise;


    public ExerciseResponse(Long exerciseId, String name, String description, String category, Boolean isCustomExercise) {
        this.exerciseId = exerciseId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.isCustomExercise = isCustomExercise;
    }

    public Long getExerciseId() {return exerciseId;}
    public void setExerciseId(Long exerciseId) {this.exerciseId = exerciseId;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}

    public Boolean getIsCustomExercise() {return isCustomExercise;}
    public void setIsCustomExercise(Boolean isCustomExercise) {this.isCustomExercise = isCustomExercise;}

}
