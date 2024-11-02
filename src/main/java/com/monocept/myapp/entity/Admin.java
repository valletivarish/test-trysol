package com.monocept.myapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "administrator") 
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message="first name cannot be blank")
    @Size(min = 2 , max = 50)
    private String firstName;

    @NotBlank(message="last name cannot be blank")
    @Size(min = 2, max = 50)
    private String lastName;

    @OneToOne(mappedBy = "admin")
    private User user;
}
