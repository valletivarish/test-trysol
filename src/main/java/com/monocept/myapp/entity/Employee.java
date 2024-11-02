package com.monocept.myapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message="first name cannot be blank")
    @Size(min = 2 , max = 50)
    private String firstName;

    @NotBlank(message="last name cannot be blank")
    @Size(min = 2, max = 50)
    private String lastName;

    @OneToOne(mappedBy = "employee")
    private User user;

    private boolean active = true;
}
