package com.example.survey;

import com.example.survey.controller.SurveyController;
import com.example.survey.entity.Survey;
import com.example.survey.entity.User;
import com.example.survey.service.SurveyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class SurveyControllerMvcTest {

    private MockMvc mockMvc;
    private SurveyService surveyService;

    @BeforeEach
    void setup() {
        SurveyController controller = new SurveyController();
        surveyService = Mockito.mock(SurveyService.class);
        ReflectionTestUtils.setField(controller, "surveyService", surveyService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void showCreateSurvey_redirectsIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/survey/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void showCreateSurvey_showsPageWhenAuthenticated() throws Exception {
        MockHttpSession session = new MockHttpSession();
        User u = new User();
        u.setId(1L);
        session.setAttribute("user", u);

        mockMvc.perform(get("/survey/create").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("create-survey"));
    }

    @Test
    void createSurvey_missingTitle_redirectsBack() throws Exception {
        MockHttpSession session = new MockHttpSession();
        User u = new User();
        u.setId(1L);
        session.setAttribute("user", u);

        mockMvc.perform(post("/survey/create").session(session)
                .param("title", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/survey/create"));
    }

    @Test
    void createSurvey_valid_createsAndRedirects() throws Exception {
        MockHttpSession session = new MockHttpSession();
        User u = new User();
        u.setId(1L);
        session.setAttribute("user", u);

        mockMvc.perform(post("/survey/create").session(session)
                .param("title", "Hi")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/survey/my-surveys"));
    }
}
