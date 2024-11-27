package com.softserve.itacademy.dto;

import com.softserve.itacademy.model.State;
import org.springframework.stereotype.Component;

@Component
public class StateDtoConverter {


    public StateDto stateToDto(State state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }
        StateDto stateDto = new StateDto();
        stateDto.setId(state.getId());
        stateDto.setName(state.getName());

        return stateDto;
    }

    public State dtoToState(State state, StateDto stateDto) {
        if (state == null || stateDto == null) {
            throw new IllegalArgumentException("State or StateDto cannot be null");
        }
        state.setName(stateDto.getName());
        return state;
    }

    public State dtoToState(StateDto stateDto) {
        if (stateDto == null) {
            throw new IllegalArgumentException("StateDto cannot be null");
        }
        State state = new State();
        state.setName(stateDto.getName());
        return state;
    }
}

