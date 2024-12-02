package com.example.ProjectDAC.controller;

import com.example.ProjectDAC.domain.CategoryBinding;
import com.example.ProjectDAC.domain.dto.AccountCategoryDTO;
import com.example.ProjectDAC.domain.dto.CampaignCategoryDTO;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.request.CategoryBindingRequest;
import com.example.ProjectDAC.domain.dto.ResCategoryBindingDTO;
import com.example.ProjectDAC.request.DeleteCategoryBindingRequest;
import com.example.ProjectDAC.service.CategoryBindingService;
import com.example.ProjectDAC.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryBindingController {
    private final CategoryBindingService categoryBindingService;
    private final UserService userService;
    public CategoryBindingController(CategoryBindingService categoryBindingService, UserService userService) {
        this.categoryBindingService = categoryBindingService;
        this.userService = userService;
    }

    @PostMapping("/category-binding")
    public ResponseEntity<ResCategoryBindingDTO> create(@Valid @RequestBody CategoryBindingRequest request) throws IdInvalidException {
        List<Long> ids = this.userService.getAnkenListFromSecurityContext();
        CategoryBinding categoryBinding = this.categoryBindingService.create(request, ids);
        ResCategoryBindingDTO response = new ResCategoryBindingDTO(categoryBinding.getId(), categoryBinding.getCategory().getId(),
                categoryBinding.getEntityId(), categoryBinding.getEntityType());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/category-binding")
    public ResponseEntity<ResCategoryBindingDTO> update(@Valid @RequestBody CategoryBindingRequest request) throws IdInvalidException {
        CategoryBinding categoryBinding = this.categoryBindingService.update(request);
        ResCategoryBindingDTO response = new ResCategoryBindingDTO(categoryBinding.getId(), categoryBinding.getCategory().getId(),
                categoryBinding.getEntityId(), categoryBinding.getEntityType());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/category-binding/account-category")
    public ResponseEntity<List<AccountCategoryDTO>> getAccountCategoryDetails() {
        List<Long> ids = this.userService.getAnkenListFromSecurityContext();
        List<AccountCategoryDTO> res = this.categoryBindingService.getAccountCategoryDetails(ids);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/category-binding/campaign-category")
    public ResponseEntity<List<CampaignCategoryDTO>> getCampaignCategoryDetails() {
        List<Long> ids = this.userService.getAnkenListFromSecurityContext();
        List<CampaignCategoryDTO> res = this.categoryBindingService.getCampaignCategoryDetails(ids);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/category-binding/delete")
    public ResponseEntity<String> delete(@Valid @RequestBody DeleteCategoryBindingRequest request) throws IdInvalidException {
        List<Long> ids = this.userService.getAnkenListFromSecurityContext();
        this.categoryBindingService.deleteCategoryBinding(request.getEntityId(), request.getTypeCategory(), ids);
        return ResponseEntity.status(HttpStatus.OK).body("Delete Successfully");
    }
}
