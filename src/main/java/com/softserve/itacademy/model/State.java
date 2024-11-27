package com.softserve.itacademy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "states")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "state_seq")
    @SequenceGenerator(name = "state_seq", sequenceName = "state_sequence", allocationSize = 1)
    @EqualsAndHashCode.Include
    private long id;

    @NotBlank(message = "The 'name' cannot be empty")
    @Column(name = "name", nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String name;

    @OneToMany(mappedBy = "state")
    private List<Task> tasks;

}