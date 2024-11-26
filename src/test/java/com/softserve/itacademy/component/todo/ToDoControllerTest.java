package com.softserve.itacademy.component.todo;

import com.softserve.itacademy.controller.ToDoController;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {ToDoController.class})
@ContextConfiguration(classes = {ToDoController.class})
public class ToDoControllerTest {
    @MockBean
    private ToDoService toDoService;
    @MockBean
    private TaskService taskService;
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetCreateToDo() throws Exception {
        ToDo expected = new ToDo();
        mockMvc.perform(MockMvcRequestBuilders.get("/todos/create/users/5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("todo"))
                .andExpect(MockMvcResultMatchers.model().attribute("todo", expected))
                .andExpect(MockMvcResultMatchers.model().attribute("ownerId", 5L))
                .andExpect(MockMvcResultMatchers.view().name("create-todo"));;
    }

    @Test
    public void testPostCreateToDo() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        when(userService.readById(1L)).thenReturn(mockUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/todos/create/users/1")
                        .param("title", "Test ToDo"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/1"));

        verify(toDoService, times(1)).create(any(ToDo.class));
    }

    @Test
    void testCreateToDoError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/todos/create/users/1")
                        .param("title", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("todo", "title"))
                .andExpect(MockMvcResultMatchers.view().name("create-todo"));
    }

    @Test
    public void testReadAllTasks() throws Exception {
        ToDo todo = new ToDo();
        todo.setId(1L);
        todo.setTitle("Test ToDo");
        todo.setOwner(new User());
        todo.getOwner().setId(1L);
        todo.setCollaborators(Collections.emptyList());

        when(toDoService.readById(1L)).thenReturn(todo);
        when(taskService.getByTodoId(1L)).thenReturn(Collections.emptyList());
        when(userService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/todos/1/tasks"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("todo", "tasks", "users"))
                .andExpect(MockMvcResultMatchers.view().name("todo-tasks"));
    }

    @Test
    public void testGetUpdateToDo() throws Exception {
        ToDo todo = new ToDo();
        todo.setId(1L);
        todo.setTitle("My ToDo");
        todo.setOwner(new User());
        todo.getOwner().setId(1L);
        when(toDoService.readById(1L)).thenReturn(todo);
        mockMvc.perform(MockMvcRequestBuilders.get("/todos/1/update/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("todo"))
                .andExpect(MockMvcResultMatchers.model().attribute("todo", todo))
                .andExpect(MockMvcResultMatchers.view().name("update-todo"));
    }

    @Test
    public void testPostUpdateToDo() throws Exception {
        ToDo todo = new ToDo();
        todo.setId(1L);
        todo.setTitle("Old ToDo");
        todo.setOwner(new User());
        todo.getOwner().setId(1L);
        when(toDoService.readById(1L)).thenReturn(todo);
        mockMvc.perform(MockMvcRequestBuilders.post("/todos/1/update/users/1")
                .param("title", "Updated Title"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/1"));
        verify(toDoService, times(1)).update(any(ToDo.class));
    }

    @Test
    public void testUpdateToDoError() throws Exception {
        ToDo todo = new ToDo();
        todo.setId(1L);
        todo.setTitle("Valid Title");
        todo.setOwner(new User());

        when(toDoService.readById(1L)).thenReturn(todo);
        when(userService.readById(1L)).thenReturn(new User());

        mockMvc.perform(MockMvcRequestBuilders.post("/todos/1/update/users/1")
                        .param("title", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("todo", "title"))
                .andExpect(MockMvcResultMatchers.view().name("update-todo"));
    }

    @Test
    public void testDeleteToDo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/todos/7/delete/users/5"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/5"));

        verify(toDoService, times(1)).delete(7L);;
    }

    @Test
    public void testGetAllToDo() throws Exception {
        User owner = new User();
        owner.setId(1L);
        when(userService.readById(1L)).thenReturn(owner);
        when(toDoService.getByUserId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/todos/all/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("todos", "user"))
                .andExpect(MockMvcResultMatchers.view().name("todos-user"));
    }

    @Test
    public void testGetAddCollaborator() throws Exception{
        ToDo todo = new ToDo();
        todo.setId(1L);
        todo.setCollaborators(new ArrayList<>());

        when(toDoService.readById(1L)).thenReturn(todo);
        when(userService.readById(2L)).thenReturn(new User());

        mockMvc.perform(MockMvcRequestBuilders.get("/todos/1/add?user_id=2"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/1/tasks"));

        verify(toDoService, times(1)).update(any(ToDo.class));
    }

    @Test
    public void testGetRemoveCollaborator() throws Exception{
        ToDo todo = new ToDo();
        todo.setId(1L);
        todo.setCollaborators(new ArrayList<>()); // Initialize collaborators list

        when(toDoService.readById(1L)).thenReturn(todo);
        when(userService.readById(2L)).thenReturn(new User());

        mockMvc.perform(MockMvcRequestBuilders.get("/todos/1/remove?user_id=2"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/1/tasks"));

        verify(toDoService, times(1)).update(any(ToDo.class));
    }

}
