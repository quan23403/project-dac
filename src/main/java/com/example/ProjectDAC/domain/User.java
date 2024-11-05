package com.example.ProjectDAC.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message =  "Email không được để trống")
    private String email;
    @NotBlank(message =  "Password không được để trống")
    private String password;
    private String firstName;
    private String lastName;
}
