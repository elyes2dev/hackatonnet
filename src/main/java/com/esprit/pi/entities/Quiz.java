package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_quiz;

    private String title;

    @OneToOne()  // Add this if you want cascading behavior
    @JoinColumn(name = "workshop_id", nullable = false, unique = true)
    @JsonBackReference  // Prevent recursion by using @JsonBackReference
    private Workshop workshop;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    private boolean isPublished; // Controls if the quiz is visible to users

    @OneToMany(mappedBy = "quiz")
    private List<UserQuizScore> userQuizScores;

    public List<UserQuizScore> getUserQuizScores() {
        return userQuizScores;
    }

    public void setUserQuizScores(List<UserQuizScore> userQuizScores) {
        this.userQuizScores = userQuizScores;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public Long getId_quiz() {
        return id_quiz;
    }

    public void setId_quiz(Long id_quiz) {
        this.id_quiz = id_quiz;
    }



    // Getters and Setters
}
