package com.example.ProjectDAC.domain.dto;

import com.example.ProjectDAC.domain.Anken;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ResLoginDTO {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private List<Anken> ankenList;
    private String token;
}
