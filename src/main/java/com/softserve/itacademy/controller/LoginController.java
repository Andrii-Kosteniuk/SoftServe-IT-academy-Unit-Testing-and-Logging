package com.softserve.itacademy.controller;

import com.softserve.itacademy.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequiredArgsConstructor
@Controller
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        log.info("Received request to login page");
        if (session.getAttribute("user_id") != null) {
            log.info("User is already logged in with user_id: {}", session.getAttribute("user_id"));
            return "redirect:/";
        }
        log.debug("Returning login view for unauthenticated user");
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(@RequestParam("username") String email,
                            @RequestParam("password") String password,
                            HttpSession session) {

        log.info("Processing login attempt for username: {}", email);
        var userOpt = userService.findByUsername(email);
        if (userOpt.isEmpty()) {
            log.warn("Login failed: Username {} not found", email);
            return "redirect:/login?error=true";
        }
        var user = userOpt.get();
        if (user.getPassword().equals("{noop}" + password)) {
            log.info("Login successful for user_id: {}, username: {}", user.getId(), email);
            session.setAttribute("username", user.getFirstName());
            session.setAttribute("user_id", user.getId());
            return "redirect:/";
        } else {
            log.warn("Login failed: Incorrect password for username: {}", email);
            return "redirect:/login?error=true";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        log.info("User with user_id: {} is logging out", session.getAttribute("user_id"));
        session.invalidate();
        log.debug("Session invalidated successfully");
        return "redirect:/login?logout=true";
    }
}
