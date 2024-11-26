package com.softserve.itacademy.component.task;

import com.softserve.itacademy.controller.TaskController;
import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.TaskPriority;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.service.StateService;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;



import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {TaskController.class})
@ContextConfiguration(classes = {TaskController.class, TaskTransformer.class})
class TaskControllerTest {

    @MockBean
    private TaskService taskService;
    @MockBean
    private ToDoService todoService;
    @MockBean
    private StateService stateService;
    @MockBean
    private TaskTransformer taskTransformer;

    @Autowired
    private MockMvc mvc;

    @Test
    void testGetCreateTaskForm() throws Exception {
        long todoId = 1L;
        ToDo mockToDo = new ToDo();
        mockToDo.setId(todoId);
        mockToDo.setTitle("Test ToDo");

        when(todoService.readById(todoId)).thenReturn(mockToDo);

        mvc.perform(get("/tasks/create/todos/" + todoId))
                .andExpect(status().isOk())
                .andExpect(view().name("create-task"))
                .andExpect(model().attributeExists("task", "todo", "priorities"))
                .andExpect(model().attribute("todo", mockToDo))
                .andExpect(model().attribute("priorities", TaskPriority.values()))
                .andDo(print());

        verify(todoService, times(1)).readById(todoId);
    }

    @Test
    void testCreateTaskSuccessfully() throws Exception {
        long todoId = 1L;
        TaskDto taskDto = new TaskDto();
        taskDto.setName("Test Task");
        taskDto.setPriority("HIGH");
        taskDto.setTodoId(todoId);

        when(taskService.create(any(TaskDto.class))).thenReturn(taskDto);

        mvc.perform(post("/tasks/create/todos/" + todoId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", taskDto.getName())
                        .param("priority", taskDto.getPriority())
                        .param("todoId", String.valueOf(taskDto.getTodoId()))
                        .param("stateId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/" + todoId + "/tasks"))
                .andDo(print());

        verify(taskService, times(1)).create(any(TaskDto.class));
    }

    @Test
    void testCreateTaskValidationErrors() throws Exception {
        long todoId = 1L;
        ToDo mockToDo = new ToDo();
        mockToDo.setId(todoId);
        mockToDo.setTitle("Test ToDo");

        when(todoService.readById(todoId)).thenReturn(mockToDo);

        mvc.perform(post("/tasks/create/todos/" + todoId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "")
                        .param("priority", "LOW")
                        .param("todoId", String.valueOf(todoId))
                        .param("stateId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-task"))
                .andExpect(model().attributeHasFieldErrors("task", "name"))
                .andExpect(model().attributeExists("priorities"))
                .andDo(print());

        verify(taskService, never()).create(any(TaskDto.class));
    }




    @Test
    void testDeleteTask() throws Exception {
        long taskId = 1L;
        long todoId = 2L;

        doNothing().when(taskService).delete(taskId);

        mvc.perform(get("/tasks/" + taskId + "/delete/todos/" + todoId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/" + todoId + "/tasks"))
                .andDo(print());

        verify(taskService, times(1)).delete(taskId);
    }

    @Test
    public void testGetUpdateTaskForm() throws Exception {
        long taskId = 1L;
        long todoId = 2L;

        Task mockTask = new Task();
        mockTask.setId(taskId);
        mockTask.setName("Test Task");
        mockTask.setPriority(TaskPriority.HIGH);

        ToDo mockTodo = new ToDo();
        mockTodo.setId(todoId);
        mockTask.setTodo(mockTodo);

        when(taskService.readById(taskId)).thenReturn(mockTask);

        TaskDto mockTaskDto = new TaskDto();
        mockTaskDto.setId(taskId);
        mockTaskDto.setName("Test Task");
        mockTaskDto.setPriority(TaskPriority.HIGH.toString());
        when(taskTransformer.convertToDto(mockTask)).thenReturn(mockTaskDto);

        mvc.perform(get("/tasks/{taskId}/update/todos/{todoId}", taskId, todoId))
                .andExpect(status().isOk())
                .andExpect(view().name("update-task"))
                .andExpect(model().attributeExists("task", "priorities"))
                .andDo(print());

        verify(taskService, times(1)).readById(taskId);
        verify(taskTransformer, times(1)).convertToDto(mockTask);
    }



    @Test
    void testUpdateTaskSuccessfully() throws Exception {
        long taskId = 1L;
        long todoId = 2L;

        TaskDto updatedTask = new TaskDto();
        updatedTask.setId(taskId);
        updatedTask.setName("Updated Task");
        updatedTask.setPriority("MEDIUM");

        when(taskService.update(any(TaskDto.class))).thenReturn(updatedTask);

        mvc.perform(post("/tasks/" + taskId + "/update/todos/" + todoId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", updatedTask.getName())
                        .param("priority", updatedTask.getPriority())
                        .param("stateId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/" + todoId + "/tasks"))
                .andDo(print());

        verify(taskService, times(1)).update(any(TaskDto.class));
    }

    @Test
    void testInvalidUrl() throws Exception {
        mvc.perform(get("/invalid-url"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
