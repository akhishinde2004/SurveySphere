package com.example.survey.repository;

import com.example.survey.entity.Survey;
import com.example.survey.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseRepository extends JpaRepository<SurveyResponse, Long> {
    List<SurveyResponse> findBySurvey(Survey survey);
    long countBySurvey(Survey survey);
}