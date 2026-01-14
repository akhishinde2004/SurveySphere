package com.example.survey.controller;

import com.example.survey.entity.Survey;
import com.example.survey.entity.SurveyResponse;
import com.example.survey.entity.User;
import com.example.survey.service.SurveyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/survey")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @GetMapping("/create")
    public String showCreateSurveyPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("survey", new Survey());
        return "create-survey";
    }

    @PostMapping("/create")
    public String createSurvey(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String purpose,
            @RequestParam(required = false) String targetAudience,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
            @RequestParam(required = false) String visibility,
            @RequestParam(required = false) String tone,
            @RequestParam(required = false) Integer estimatedTime,
            @RequestParam(required = false) String instructions,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if (title == null || title.trim().isEmpty()) {
            return "redirect:/survey/create";
        }

        Survey survey = new Survey();
        survey.setTitle(title.trim());
        survey.setDescription(description != null ? description.trim() : null);
        survey.setCategory(category != null ? category.trim() : null);
        survey.setPurpose(purpose != null ? purpose.trim() : null);
        survey.setTargetAudience(targetAudience != null ? targetAudience.trim() : null);
        survey.setExpiryDate(expiryDate);
        survey.setVisibility(visibility != null ? visibility : "public");
        survey.setTone(tone != null ? tone : "neutral");
        survey.setEstimatedTime(estimatedTime);
        survey.setInstructions(instructions != null ? instructions.trim() : null);

        surveyService.createSurvey(survey, user);
        return "redirect:/survey/my-surveys";
    }

    @GetMapping("/my-surveys")
    public String mySurveys(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Survey> surveys = surveyService.getUserSurveys(user);
        model.addAttribute("surveys", surveys);
        return "my-surveys";
    }

    @GetMapping("/public")
    public String publicSurveys(HttpSession session, Model model) {
        List<Survey> surveys = surveyService.getPublicSurveys();
        model.addAttribute("surveys", surveys);
        return "public-surveys";
    }

    @GetMapping("/take/{id}")
    public String takeSurvey(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Survey> surveyOpt = surveyService.getSurveyById(id);
        if (surveyOpt.isEmpty()) {
            return "redirect:/dashboard";
        }

        Survey survey = surveyOpt.get();
        if (survey.getExpiryDate() != null && survey.getExpiryDate().isBefore(LocalDate.now())) {
            model.addAttribute("error", "This survey has expired");
            return "dashboard";
        }

        model.addAttribute("survey", survey);
        return "take-survey";
    }

    @PostMapping("/submit/{id}")
    public String submitResponse(
            @PathVariable Long id,
            @RequestParam(required = false) Integer overallRating,
            @RequestParam(required = false) Integer question1Rating,
            @RequestParam(required = false) Integer question2Rating,
            @RequestParam(required = false) Integer question3Rating,
            @RequestParam(required = false) Boolean yesNoAnswer,
            @RequestParam(required = false) String feedback,
            @RequestParam(required = false) String suggestions,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Survey> surveyOpt = surveyService.getSurveyById(id);
        if (surveyOpt.isEmpty()) {
            return "redirect:/dashboard";
        }

        Survey survey = surveyOpt.get();

        SurveyResponse response = new SurveyResponse();
        response.setOverallRating(overallRating);
        response.setQuestion1Rating(question1Rating);
        response.setQuestion2Rating(question2Rating);
        response.setQuestion3Rating(question3Rating);
        response.setYesNoAnswer(yesNoAnswer);
        response.setFeedback(feedback != null ? feedback.trim() : null);
        response.setSuggestions(suggestions != null ? suggestions.trim() : null);

        surveyService.submitResponse(response, survey);
        return "redirect:/survey/thank-you";
    }

    @GetMapping("/results/{id}")
    public String surveyResults(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Survey> surveyOpt = surveyService.getSurveyById(id);
        if (surveyOpt.isEmpty()) {
            return "redirect:/dashboard";
        }

        Survey survey = surveyOpt.get();
        if (!survey.getCreator().getId().equals(user.getId())) {
            return "redirect:/dashboard";
        }

        List<SurveyResponse> responses = surveyService.getSurveyResponses(survey);
        double averageRating = surveyService.getAverageRating(survey);

        model.addAttribute("survey", survey);
        model.addAttribute("responses", responses);
        model.addAttribute("averageRating", averageRating);

        return "survey-results";
    }

    @GetMapping("/thank-you")
    public String thankYou(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return "thank-you";
    }
}