package com.example.survey;

import com.example.survey.controller.AuthController;
import com.example.survey.entity.User;
import com.example.survey.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerMvcTest {

    private MockMvc mockMvc;
    private UserService userService;
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setup() {
        AuthController controller = new AuthController();
        userService = Mockito.mock(UserService.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);

        ReflectionTestUtils.setField(controller, "userService", userService);
        ReflectionTestUtils.setField(controller, "authenticationManager", authenticationManager);

        org.springframework.web.servlet.view.InternalResourceViewResolver viewResolver =
            new org.springframework.web.servlet.view.InternalResourceViewResolver();
        viewResolver.setPrefix("");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(viewResolver).build();
    }

    @Test
    void register_success_redirectsToDashboard() throws Exception {
        User saved = new User();
        saved.setId(1L);
        saved.setEmail("a@b.com");
        when(userService.registerUser("Name", "a@b.com", "pw")).thenReturn(saved);

        Authentication auth = Mockito.mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Name")
                .param("email", "a@b.com")
                .param("password", "pw")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void register_missingFields_returnsRegister() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "")
                .param("email", "")
                .param("password", "")
        )
                .andExpect(status().isOk());
    }
}
