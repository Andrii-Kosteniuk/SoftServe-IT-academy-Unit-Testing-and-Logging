package com.softserve.itacademy.service;

import com.softserve.itacademy.dto.StateDto;
import com.softserve.itacademy.dto.StateDtoConverter;
import com.softserve.itacademy.model.State;
import com.softserve.itacademy.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class StateService {

    private final StateRepository stateRepository;
    private final StateDtoConverter stateDtoConverter;

    public State create(StateDto stateDto) {
        if (stateDto == null) {
            throw new IllegalArgumentException("State cannot be null");
        }
        if (stateDto.getName() != null && stateRepository.findByName(stateDto.getName()) != null) {
            throw new IllegalArgumentException("State with name '" + stateDto.getName() + "' already exists");
        }

        State state = stateDtoConverter.dtoToState(stateDto);
        return stateRepository.save(state);

    }

    public State readById(long id) {
        return stateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("State with id " + id + " not found"));
    }

    public StateDto update(StateDto stateDto) {
        if (stateDto == null || stateDto.getId() == null) {
            throw new IllegalArgumentException("State or State ID cannot be null");
        }

        State existingState = stateRepository.findById(stateDto.getId())
                .orElseThrow(() -> new RuntimeException("State with ID " + stateDto.getId() + " not found"));

        State updatedState = stateDtoConverter.dtoToState(existingState, stateDto);
        State savedState = stateRepository.save(updatedState);
        return stateDtoConverter.stateToDto(savedState);
    }

    public void delete(long id) {
        State state = readById(id);
        if (! state.getTasks().isEmpty()) {
            throw new RuntimeException();
        }
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
