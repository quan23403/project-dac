package com.example.ProjectDAC.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private List<Long> listAnkenId;
    private Set<String> roles;
}
