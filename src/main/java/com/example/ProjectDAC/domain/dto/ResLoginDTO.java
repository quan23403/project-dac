package com.example.ProjectDAC.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResLoginDTO {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private String token;
}
