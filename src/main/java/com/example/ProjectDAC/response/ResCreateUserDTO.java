package com.example.ProjectDAC.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
}
