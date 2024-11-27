package com.softserve.itacademy.component.user;

import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UpdateUserDto;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    public void getAllUsersTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(view().name("users-list"));
    }

    @Test
    public void createUserPageTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/create"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("create-user"));
    }

    @Test
    public void createUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/create")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("password", "Password1!")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/todos/all/users/*"));
    }


    @Test
    public void readUserTest() throws Exception {
        User user = userService.getAll().get(0);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + user.getId() + "/read"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", hasProperty("id", is(user.getId()))))
                .andExpect(view().name("user-info"));
    }

    @Test
    public void updateUserPageTest() throws Exception {
        User user = userService.getAll().get(0);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + user.getId() + "/update"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().attribute("user", hasProperty("id", is(user.getId()))))
                .andExpect(view().name("update-user"));
    }

    @Test
    public void updateUserTest() throws Exception {
        User user = userService.getAll().get(0);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/" + user.getId() + "/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", String.valueOf(user.getId()))
                        .param("firstName", "Updatedname")
                        .param("lastName", "Updatedlastname")
                        .param("email", "updated.email@example.com")
                        .param("password", "UpdatedPassword1!")
                        .param("role", "USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/" + user.getId() + "/read"));
    }


    @Test
    public void deleteUserTest() throws Exception {
        User user = userService.getAll().get(0);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + user.getId() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/all"));
    }
}
