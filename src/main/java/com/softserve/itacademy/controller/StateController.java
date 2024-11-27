package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.StateDto;
import com.softserve.itacademy.dto.StateDtoConverter;
import com.softserve.itacademy.model.State;
import com.softserve.itacademy.service.StateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/states")
@RequiredArgsConstructor
public class StateController {

    private final StateService stateService;
    private final StateDtoConverter stateDtoConverter;

    @GetMapping()
    public String listStates(Model model) {
        log.info("Received request to get all states");
        model.addAttribute("states", stateService.findAll());
        return "state/state-list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        log.info("Received request to render create state page");
        model.addAttribute("stateDto", new StateDto());
        return "state/state-create";
    }

    @PostMapping("/create")
    public String createState(@Valid @ModelAttribute("stateDto") StateDto stateDto,
                              BindingResult result,
                              Model model) {
        log.info("Processing request to create a new state");
        if (result.hasErrors()) {
            log.error("Validation failed for creating state: {}", result.getAllErrors());
            model.addAttribute("stateDto", stateDto);
            return "state/state-create";
        }

        try {
            stateService.create(stateDtoConverter.dtoToState(stateDto));
            log.info("State  was created successfully");
        } catch (IllegalArgumentException ex) {
            log.error("Failed to create state due to: {}", ex.getMessage());
            return "redirect:/states/error";
        }
        return "redirect:/states";
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable("id") Long id, Model model) {
        log.info("Received request to render update page for state with id: {}", id);
        State state = stateService.readById(id);
        model.addAttribute("stateDto", state);
        return "state/state-update";
    }

    @PostMapping("/{id}/update")
    public String update(@Valid @ModelAttribute("stateDto") StateDto stateDto,
                         BindingResult result,
                         @PathVariable("id") Long id,
                         Model model) {
        log.info("Processing update for state with id: {}", id);
        if (result.hasErrors()) {
            log.error("Validation errors during state update: {}", result.getAllErrors());
            model.addAttribute("stateDto", stateDto);
            return "state/state-update";
        }

        try {
            stateService.update(stateDtoConverter.dtoToState(stateDto));
            log.info("Successfully updated state: {}", stateDto);
        } catch (IllegalArgumentException ex) {
            log.error("Failed to update state with id {} due to: {}", id, ex.getMessage());
            return "redirect:/state/error";
        }
        return "redirect:/states";
    }

    @GetMapping("/error")
    public String showErrorPage(Model model) {
        log.info("Rendering general error page");
        model.addAttribute("code", 400);
        model.addAttribute("message", "Something went wrong. Please check the data you provided and try again!");
        return "state/error";
    }

    @GetMapping("/{id}/remove")
    public String delete(@PathVariable("id") Long id) {
        log.info("Received request to delete state with id: {}", id);
        try {
            stateService.delete(id);
            log.info("Successfully deleted state with id: {}", id);
        } catch (RuntimeException ex) {
            log.error("Failed to delete state with id {}: {}", id, ex.getMessage());
            return "redirect:/states/error/delete";
        }

        return "redirect:/states";
    }

    @GetMapping("/error/delete")
    public String errorDeleteState(Model model) {
        log.info("Redirected on error page from delete state");
        model.addAttribute("code", 400);
        model.addAttribute("message", "You can not delete this state because some tasks have assigned to it");
        return "state/error";
    }

}
