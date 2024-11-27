package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UpdateUserDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/create")
    public String create(Model model) {
        log.info("Opening user creation page");
        model.addAttribute("user", new CreateUserDto());
        return "create-user";
    }

    @PostMapping("/create")
    public String create(@Validated @ModelAttribute("user") User user, BindingResult result) {
        log.info("Attempting to create a new user");
        if (result.hasErrors()) {
            log.warn("Validation failed during user creation: {}", result.getAllErrors());
            return "create-user";
        }
        user.setRole(UserRole.USER);
        User newUser = userService.create(user);
        log.info("User created successfully with ID {}", newUser.getId());
        return "redirect:/todos/all/users/" + newUser.getId();
    }

    @GetMapping("/{id}/read")
    public String read(@PathVariable long id, Model model) {
        log.info("Reading user with ID {}", id);
        User user = userService.readById(id);
        model.addAttribute("user", user);
        log.debug("User data: {}", user);
        return "user-info";
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable long id, Model model) {
        log.info("Opening update page for user with ID {}", id);
        User user = userService.readById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", UserRole.values());
        return "update-user";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable long id, Model model,
                         @Validated @ModelAttribute("user") UpdateUserDto updateUserDto, BindingResult result) {
        log.info("Attempting to update user with ID {}", id);
        UserDto oldUser = userService.findByIdThrowing(id);

        if (result.hasErrors()) {
            log.warn("Validation failed during update for user ID {}: {}", id, result.getAllErrors());
            updateUserDto.setRole(oldUser.getRole());
            model.addAttribute("roles", UserRole.values());
            return "update-user";
        }

        userService.update(updateUserDto);
        log.info("User with ID {} updated successfully", id);
        return "redirect:/users/" + id + "/read";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") long id, HttpSession session) {
        log.info("Deleting user with ID {}", id);
        userService.delete(id);
        log.info("User with ID {} deleted successfully", id);
        return "redirect:/users/all";
    }

    @GetMapping("/all")
    public String getAll(Model model) {
        log.info("Fetching all users");
        model.addAttribute("users", userService.getAll());
        log.debug("All users fetched successfully");
        return "users-list";
    }
}
