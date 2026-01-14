package com.example.survey;

import com.example.survey.entity.Survey;
import com.example.survey.repository.SurveyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Disabled("Integration flow is environment-sensitive; run locally when needed")
public class IntegrationFlowTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SurveyRepository surveyRepository;

    private String extractSessionCookie(ResponseEntity<String> response) {
        List<String> setCookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (setCookies == null || setCookies.isEmpty()) return null;
        // take first cookie (JSESSIONID)
        String cookie = setCookies.get(0);
        int idx = cookie.indexOf(';');
        return idx > 0 ? cookie.substring(0, idx) : cookie;
    }

    private static class CsrfInfo {
        String token;
        String cookie;
    }

    private CsrfInfo fetchCsrf(String path) {
        return fetchCsrf(path, null);
    }

    private CsrfInfo fetchCsrf(String path, String cookie) {
        HttpHeaders headers = new HttpHeaders();
        if (cookie != null) headers.add(HttpHeaders.COOKIE, cookie);
        HttpEntity<Void> req = new HttpEntity<>(headers);
        ResponseEntity<String> page = restTemplate.exchange(path, HttpMethod.GET, req, String.class);
        CsrfInfo info = new CsrfInfo();
        info.cookie = extractSessionCookie(page);
        String body = page.getBody();
        if (body != null) {
            // find the _csrf hidden input value
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("name=\\\"_csrf\\\"[^>]*value=\\\"([^\\\"]+)\\\"").matcher(body);
            if (m.find()) {
                info.token = m.group(1);
            }
        }
        return info;
    }

    @Test
    void registerCreateSurveySubmitAndViewResults() throws Exception {
        // 1) Register user
        CsrfInfo csrf = fetchCsrf("/register");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        if (csrf.cookie != null) headers.add(HttpHeaders.COOKIE, csrf.cookie);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("name", "Test User");
        form.add("email", "testuser@example.com");
        form.add("password", "password123");
        if (csrf.token != null) form.add("_csrf", csrf.token);

        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(form, headers);
        ResponseEntity<String> regResp = restTemplate.postForEntity("/register", req, String.class);
        assertThat(regResp.getStatusCode().is2xxSuccessful() || regResp.getStatusCode().is3xxRedirection()).isTrue();

        String sessionCookie = extractSessionCookie(regResp);
        if (sessionCookie == null) sessionCookie = csrf.cookie;
        assertThat(sessionCookie).isNotNull();

        // 2) Create survey using session cookie
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add(HttpHeaders.COOKIE, sessionCookie);

        // fetch CSRF token for /survey/create with same session
        CsrfInfo createCsrf = fetchCsrf("/dashboard", sessionCookie);

        MultiValueMap<String, String> surveyForm = new LinkedMultiValueMap<>();
        surveyForm.add("title", "Integration Test Survey");
        surveyForm.add("description", "A survey created by integration test");

        if (createCsrf != null && createCsrf.token != null) surveyForm.add("_csrf", createCsrf.token);
        HttpEntity<MultiValueMap<String, String>> createReq = new HttpEntity<>(surveyForm, headers);
        ResponseEntity<String> createResp = restTemplate.postForEntity("/survey/create", createReq, String.class);
        assertThat(createResp.getStatusCode().is2xxSuccessful() || createResp.getStatusCode().is3xxRedirection()).isTrue();

        // Find saved survey
        List<Survey> found = surveyRepository.findByVisibility("public");
        Survey survey = found.stream().filter(s -> "Integration Test Survey".equals(s.getTitle())).findFirst().orElse(null);
        assertThat(survey).isNotNull();

        // 3) Submit a response
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add(HttpHeaders.COOKIE, sessionCookie);
        CsrfInfo submitCsrf = fetchCsrf("/survey/take/" + survey.getId(), sessionCookie);

        MultiValueMap<String, String> respForm = new LinkedMultiValueMap<>();
        respForm.add("overallRating", "4");
        respForm.add("question1Rating", "4");
        respForm.add("question2Rating", "4");
        respForm.add("question3Rating", "4");
        respForm.add("yesNoAnswer", "true");
        respForm.add("feedback", "Great survey");

        if (submitCsrf != null && submitCsrf.token != null) respForm.add("_csrf", submitCsrf.token);
        HttpEntity<MultiValueMap<String, String>> submitReq = new HttpEntity<>(respForm, headers);
        ResponseEntity<String> submitResp = restTemplate.postForEntity("/survey/submit/" + survey.getId(), submitReq, String.class);
        assertThat(submitResp.getStatusCode().is2xxSuccessful() || submitResp.getStatusCode().is3xxRedirection()).isTrue();

        // 4) View results
        headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, sessionCookie);
        HttpEntity<Void> viewReq = new HttpEntity<>(headers);
        ResponseEntity<String> resultsResp = restTemplate.exchange("/survey/results/" + survey.getId(), HttpMethod.GET, viewReq, String.class);
        assertThat(resultsResp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resultsResp.getBody()).contains("Total Responses");
    }
}
