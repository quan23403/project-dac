package com.example.ProjectDAC.domain.dto;

import com.example.ProjectDAC.domain.Anken;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class ResUserDTO {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private List<Long> ankenListId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
