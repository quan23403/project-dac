package com.example.ProjectDAC.controller;

import com.example.ProjectDAC.domain.Anken;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.service.AnkenService;
import jakarta.validation.Valid;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AnkenController {
    private final AnkenService ankenService;
    public AnkenController(AnkenService ankenService) {
        this.ankenService = ankenService;
    }
    @PostMapping("/anken")
    public ResponseEntity<Anken> create(@Valid @RequestBody Anken anken) throws IdInvalidException {
        if(this.ankenService.isAnkenNameExist(anken.getName())) {
            throw new IdInvalidException("Anken already exists");
        }
        Anken newAnken = this.ankenService.create(anken);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAnken);
    }

    @GetMapping("/anken")
    public ResponseEntity<List<Anken>> getAllAnken() {
        List<Anken> ankens = this.ankenService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(ankens);
    }
}
