package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.StateDtoConverter;
import com.softserve.itacademy.service.StateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StateController.class)
class StateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StateService stateService;

    @MockBean
    private StateDtoConverter stateDtoConverter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listStates_shouldReturnStateListPage() throws Exception {
        mockMvc.perform(get("/states"))
                .andExpect(status().isOk())
                .andExpect(view().name("state/state-list"))
                .andExpect(model().attributeExists("states"))
                .andExpect(forwardedUrl(null));

    }

    @Test
    void test_create_shouldShowCreateStatePage() throws Exception {
        mockMvc.perform(get("/states/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("state/state-create"))
                .andExpect(model().attributeExists("stateDto"));
    }

    @Test
    void test_createState_validDto_shouldRedirectToStates() throws Exception {
        mockMvc.perform(post("/states/create")
                        .param("name", "New"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/states"));
    }

    @Test
    void test_updateState_validDto_shouldRedirectToStates() throws Exception {
        mockMvc.perform(post("/states/{id}/update", 7)
                        .param("name", "Updated"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/states"));
    }

    @Test
    void updateState_invalidDto_shouldReturnUpdatePage() throws Exception {
        mockMvc.perform(post("/states/1/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("state/state-update"))
                .andExpect(model().attributeExists("stateDto"))
                .andExpect(model().hasErrors());

    }

    @Test
    void test_createState_invalidDto() throws Exception {
        mockMvc.perform(post("/states/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("state/state-create"))
                .andExpect(model().hasErrors());
    }


    @Test
    void test_deleteState_shouldRedirectToStates() throws Exception {
        mockMvc.perform(get("/states/{id}/remove", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/states"));
    }

    @Test
    void test_deleteState_withException_shouldRedirectToErrorPage() throws Exception {
        doThrow(new RuntimeException()).when(stateService).delete(1L);

        mockMvc.perform(get("/states/{id}/remove", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/states/error/delete"));

        verify(stateService, times(1)).delete(1L);
    }

    @Test
    void error_shouldShowErrorPage() throws Exception {
        mockMvc.perform(get("/states/error"))
                .andExpect(status().isOk())
                .andExpect(view().name("state/error"))
                .andExpect(model().attributeExists("code"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    void errorDeleteState_shouldShowErrorDeletePage() throws Exception {
        mockMvc.perform(get("/states/error/delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("state/error"))
                .andExpect(model().attributeExists("code"))
                .andExpect(model().attributeExists("message"));
    }
}