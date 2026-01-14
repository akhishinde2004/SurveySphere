package com.example.survey;

import com.example.survey.entity.User;
import com.example.survey.repository.UserRepository;
import com.example.survey.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Test
    void registerUser_encodesPassword_andSaves() {
        UserRepository repo = Mockito.mock(UserRepository.class);
        when(repo.existsByEmail("new@example.com")).thenReturn(false);
        when(repo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserService svc = new UserService(new BCryptPasswordEncoder());
        // inject repository via reflection (simple setter not available) - use Mockito to set field
        org.springframework.test.util.ReflectionTestUtils.setField(svc, "userRepository", repo);

        User u = svc.registerUser("New User", "new@example.com", "secret");
        assertThat(u).isNotNull();
        assertThat(u.getEmail()).isEqualTo("new@example.com");
        assertThat(u.getPassword()).isNotEqualTo("secret");
        assertThat(new BCryptPasswordEncoder().matches("secret", u.getPassword())).isTrue();
    }

    @Test
    void registerUser_returnsNull_ifEmailExists() {
        UserRepository repo = Mockito.mock(UserRepository.class);
        when(repo.existsByEmail("exists@example.com")).thenReturn(true);

        UserService svc = new UserService(new BCryptPasswordEncoder());
        org.springframework.test.util.ReflectionTestUtils.setField(svc, "userRepository", repo);

        User u = svc.registerUser("Name", "exists@example.com", "pw");
        assertThat(u).isNull();
    }
}
