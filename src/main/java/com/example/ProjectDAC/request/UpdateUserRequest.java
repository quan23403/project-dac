package com.example.ProjectDAC.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateUserRequest {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private List<Long> listAnkenId;
}
