package com.example.ProjectDAC.service;

import com.example.ProjectDAC.domain.Anken;
import com.example.ProjectDAC.repository.AnkenRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnkenService {
    private final AnkenRepository ankenRepository;
    public AnkenService(AnkenRepository ankenRepository) {
        this.ankenRepository = ankenRepository;
    }
    public Anken create(Anken anken) {
        return this.ankenRepository.save(anken);
    }

    public boolean isAnkenNameExist(String name) {
        return this.ankenRepository.existsByName(name);
    }

    public List<Anken> getAll() {
        return this.ankenRepository.findAll();
    }

    public Optional<Anken> getAnkenById(Long id) {
        return this.ankenRepository.findById(id);
    }
}
