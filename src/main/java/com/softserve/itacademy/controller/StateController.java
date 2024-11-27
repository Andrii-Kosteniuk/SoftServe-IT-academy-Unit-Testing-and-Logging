package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.StateDto;
import com.softserve.itacademy.dto.StateDtoConverter;
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
        log.info("Received request to create state from get");
        model.addAttribute("stateDto", new StateDto());
        return "state/state-create";
    }

    @PostMapping("/create")
    public String createState(@Valid @ModelAttribute("stateDto") StateDto stateDto,
                              BindingResult result,
                              Model model) {

        if (result.hasErrors()) {
            model.addAttribute("stateDto", stateDto);
            log.error("Error validation in post method create! Caused by {}", result.getAllErrors());
            return "state/state-create";
        }

        try {
            stateService.create(stateDto);
            log.info("Created new state {}", stateDto);
        } catch (IllegalArgumentException ex) {
            log.error("Exception during creating state! Caused by {}", ex.getMessage());
            return "redirect:/states/error";
        }
        return "redirect:/states";
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable("id") Long id, Model model) {
        log.info("Received request to update state from get");
        StateDto stateDto = stateDtoConverter.stateToDto(stateService.readById(id));
        model.addAttribute("stateDto", stateDto);
        return "state/state-update";
    }

    @PostMapping("/{id}/update")
    public String update(@Valid @ModelAttribute("stateDto") StateDto stateDto,
                         BindingResult result,
                         @PathVariable("id") Long id,
                         Model model) {
        log.info("Post update called with state id: {}", id);
        if (result.hasErrors()) {
            model.addAttribute("stateDto", stateDto);
            log.error("Validation errors: {}", result.getAllErrors());
            return "state/state-update";
        }

        try {
            stateService.update(stateDto);
            log.info("State {} was updated", stateDto.getName());
        } catch (IllegalArgumentException ex) {
            log.error("Exception during updating state was caused by {}", ex.getMessage());
            return "redirect:/state/error";
        }
        return "redirect:/states";
    }

    @GetMapping("/error")
    public String showErrorPage(Model model) {
        log.info("Redirected on common error page");
        model.addAttribute("code", 400);
        model.addAttribute("message", "Something went wrong. Please check the data you provided and try again!");
        return "state/error";
    }

    @GetMapping("/{id}/remove")
    public String delete(@PathVariable("id") Long id) {
        log.info("Received request to delete state");
        try {
            stateService.delete(id);
            log.info("Deleted state by id: {}", id);
        } catch (RuntimeException ex) {
            log.error("Exception during deleting state: {}", ex.getMessage());
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
