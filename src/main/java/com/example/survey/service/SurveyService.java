package com.example.survey.service;

import com.example.survey.entity.Survey;
import com.example.survey.entity.SurveyResponse;
import com.example.survey.entity.User;
import com.example.survey.repository.SurveyRepository;
import com.example.survey.repository.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SurveyService {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private ResponseRepository responseRepository;

    public Survey createSurvey(Survey survey, User creator) {
        survey.setCreator(creator);
        return surveyRepository.save(survey);
    }

    public List<Survey> getUserSurveys(User user) {
        return surveyRepository.findByCreator(user);
    }

    public List<Survey> getPublicSurveys() {
        return surveyRepository.findByVisibility("public");
    }

    public Optional<Survey> getSurveyById(Long id) {
        return surveyRepository.findById(id);
    }

    public SurveyResponse submitResponse(SurveyResponse response, Survey survey) {
        response.setSurvey(survey);
        return responseRepository.save(response);
    }

    public List<SurveyResponse> getSurveyResponses(Survey survey) {
        return responseRepository.findBySurvey(survey);
    }

    public double getAverageRating(Survey survey) {
        List<SurveyResponse> responses = responseRepository.findBySurvey(survey);
        if (responses.isEmpty()) {
            return 0.0;
        }
        double sum = responses.stream()
                .filter(r -> r.getOverallRating() != null)
                .mapToInt(SurveyResponse::getOverallRating)
                .sum();
        long count = responses.stream()
                .filter(r -> r.getOverallRating() != null)
                .count();
        return count > 0 ? sum / count : 0.0;
    }
}