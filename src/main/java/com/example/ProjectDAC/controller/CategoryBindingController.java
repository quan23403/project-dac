package com.example.ProjectDAC.controller;

import com.example.ProjectDAC.domain.CategoryBinding;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.request.CategoryBindingRequest;
import com.example.ProjectDAC.domain.dto.ResCategoryBindingDTO;
import com.example.ProjectDAC.service.CategoryBindingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryBindingController {
    private final CategoryBindingService categoryBindingService;
    public CategoryBindingController(CategoryBindingService categoryBindingService) {
        this.categoryBindingService = categoryBindingService;
    }

    @PostMapping("/category-binding")
    public ResponseEntity<ResCategoryBindingDTO> create(@Valid @RequestBody CategoryBindingRequest request) throws IdInvalidException {
        CategoryBinding categoryBinding = this.categoryBindingService.create(request);
        ResCategoryBindingDTO response = new ResCategoryBindingDTO(categoryBinding.getId(), categoryBinding.getCategory().getId(),
                categoryBinding.getEntityId(), categoryBinding.getEntityType());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/category-binding/update")
    public ResponseEntity<ResCategoryBindingDTO> update(@Valid @RequestBody CategoryBindingRequest request) throws IdInvalidException {
        CategoryBinding categoryBinding = this.categoryBindingService.update(request);
        ResCategoryBindingDTO response = new ResCategoryBindingDTO(categoryBinding.getId(), categoryBinding.getCategory().getId(),
                categoryBinding.getEntityId(), categoryBinding.getEntityType());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
