package com.example.ProjectDAC.repository;

import com.example.ProjectDAC.domain.Anken;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnkenRepository extends JpaRepository<Anken, Long> {
    boolean existsByName(String name);

    Anken findByName(@NotBlank(message = "Name's Anken can not be blank") String name);

}
