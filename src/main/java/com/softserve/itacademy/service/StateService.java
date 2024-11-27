package com.softserve.itacademy.service;

import com.softserve.itacademy.dto.StateDto;
import com.softserve.itacademy.dto.StateDtoConverter;
import com.softserve.itacademy.model.State;
import com.softserve.itacademy.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class StateService {

    private final StateRepository stateRepository;
    private final StateDtoConverter stateDtoConverter;

    public State create(State state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }
        if (state.getName() != null && stateRepository.findByName(state.getName()) != null) {
            throw new IllegalArgumentException("State with name '" + state.getName() + "' already exists");
        }

//        State state = stateDtoConverter.dtoToState(state);
        return stateRepository.save(state);

    }

    public State readById(long id) {
        return stateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("State with id " + id + " not found"));
    }

    public State update(State state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }

        State existingState = stateRepository.findById(state.getId())
                .orElseThrow(() -> new RuntimeException("State with ID " + state.getId() + " not found"));

        existingState.setName(state.getName());
        existingState.setTasks(state.getTasks());
        return stateRepository.save(existingState);

    }

    public void delete(long id) {
        State state = readById(id);

        stateRepository.delete(state);
    }

    public List<State> getAll() {
        return stateRepository.findAllByOrderById();
    }

    public State getByName(String name) {
        State state = stateRepository.findByName(name);

        if (state != null) {
            return state;
        }
        throw new RuntimeException("State with name '" + name + "' not found");
    }

    public List<StateDto> findAll() {
        List<StateDto> dtos = stateRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
        return dtos;
    }

    private StateDto toDto(State state) {
        StateDto stateDto = new StateDto();
        stateDto.setId(state.getId());
        stateDto.setName(state.getName());
        return stateDto;
    }
}
