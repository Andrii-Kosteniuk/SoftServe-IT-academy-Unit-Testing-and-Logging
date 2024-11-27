package com.softserve.itacademy.controller;

import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.HttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private HttpSession session;

    @Test
    void login_shouldRedirectToHomeIfUserLoggedIn() throws Exception {
        mockMvc.perform(get("/login").sessionAttr("user_id", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void login_shouldShowLoginPageIfUserNotLoggedIn() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void shouldRedirectToHomeOnSuccessfulLogin() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "user@mail.com")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));

    }

    @Test
    void shouldRedirectToLoginWithErrorForInvalidUsername() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "invalid@mail.com")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));

    }


    @Test
    void logout_shouldRedirectToLoginAndAfterLogout() throws Exception {
        mockMvc.perform(post("/logout").sessionAttr("user_id", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout=true"));

    }
}
