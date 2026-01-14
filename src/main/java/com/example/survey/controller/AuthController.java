package com.example.survey.controller;

import com.example.survey.entity.User;
import com.example.survey.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "index";
    }

    @GetMapping("/register")
    public String showRegisterPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }

        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "All fields are required");
            return "register";
        }

        User user = userService.registerUser(name.trim(), email.trim(), password);
        if (user == null) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }

        // Programmatically authenticate the newly registered user
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email.trim(), password)
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        // keep session attribute for backward compatibility where controllers read it
        session.setAttribute("user", user);
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }

        if (email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Email and password are required");
            return "login";
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email.trim(), password)
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            User user = userService.findById(((com.example.survey.entity.User) userService.loginUser(email.trim(), password)).getId());
            session.setAttribute("user", user);
        } catch (Exception ex) {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        return "redirect:/";
    }
}