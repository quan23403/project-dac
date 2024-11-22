package com.example.ProjectDAC.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "anken")
@Getter
@Setter
public class Anken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "Name's Anken can not be blank")
    private String name;

    @OneToMany(mappedBy = "anken", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Account> accountList;

    @OneToMany(mappedBy = "anken", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Category> categoryList;
}
