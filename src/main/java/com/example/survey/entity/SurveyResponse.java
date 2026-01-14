package com.example.survey.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "survey_responses")
public class SurveyResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "overall_rating")
    private Integer overallRating; // 1-5

    @Column(name = "question_1_rating")
    private Integer question1Rating; // Likert scale 1-5

    @Column(name = "question_2_rating")
    private Integer question2Rating; // Likert scale 1-5

    @Column(name = "question_3_rating")
    private Integer question3Rating; // Likert scale 1-5

    @Column(name = "yes_no_answer")
    private Boolean yesNoAnswer;

    @Column(length = 2000)
    private String feedback;

    @Column(length = 500)
    private String suggestions;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    public SurveyResponse() {
        this.submittedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Integer overallRating) {
        this.overallRating = overallRating;
    }

    public Integer getQuestion1Rating() {
        return question1Rating;
    }

    public void setQuestion1Rating(Integer question1Rating) {
        this.question1Rating = question1Rating;
    }

    public Integer getQuestion2Rating() {
        return question2Rating;
    }

    public void setQuestion2Rating(Integer question2Rating) {
        this.question2Rating = question2Rating;
    }

    public Integer getQuestion3Rating() {
        return question3Rating;
    }

    public void setQuestion3Rating(Integer question3Rating) {
        this.question3Rating = question3Rating;
    }

    public Boolean getYesNoAnswer() {
        return yesNoAnswer;
    }

    public void setYesNoAnswer(Boolean yesNoAnswer) {
        this.yesNoAnswer = yesNoAnswer;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }
}