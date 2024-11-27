package com.softserve.itacademy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StateDto {

    private Long id;
    @NotBlank(message = "Name can not be empty")
    @Size(min = 3, max = 15, message = "The name should has length between 3 and 15 characters")
    private String name;
}
