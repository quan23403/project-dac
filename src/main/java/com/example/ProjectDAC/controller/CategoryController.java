package com.example.ProjectDAC.controller;

import com.example.ProjectDAC.domain.Category;
import com.example.ProjectDAC.domain.User;
import com.example.ProjectDAC.domain.dto.ResCategoryBindingDTO;
import com.example.ProjectDAC.domain.dto.ResCategoryDTO;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.request.CreateCategoryRequest;
import com.example.ProjectDAC.request.UpdateCategoryRequest;
import com.example.ProjectDAC.service.CategoryService;
import com.example.ProjectDAC.service.UserService;
import com.example.ProjectDAC.util.JwtUtils;
import com.example.ProjectDAC.util.constant.ETypeCategory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CategoryController {
    private final CategoryService categoryService;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    public CategoryController(CategoryService categoryService, UserService userService,JwtUtils jwtUtils) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }
    @PostMapping("/category")
    public ResponseEntity<Category> create(@Valid @RequestBody CreateCategoryRequest request) throws IdInvalidException {
        if (categoryService.isExistedCategoryByName(request.getName())) {
            throw new IdInvalidException("Category's Name already exists" );
        }
        try {
            // Tạo category mới và lưu vào cơ sở dữ liệu
            Category newCategory = categoryService.create(request);
            // Trả về 201 Created và đối tượng Category mới
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(newCategory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/category")
    public ResponseEntity<List<ResCategoryDTO>> getListCategories(HttpServletRequest request) throws IdInvalidException {
        String token = request.getHeader("Authorization");
        List<Long> ids = this.jwtUtils.getAnkenListFromToken(token);
        List<ResCategoryDTO> listCategory = this.categoryService.getCategories(ids);
        return ResponseEntity.status(HttpStatus.OK).body(listCategory);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Category> category = this.categoryService.getCategory(id);
        if(category.isEmpty()) {
            throw new IdInvalidException("Category does not exist");
        }
        return ResponseEntity.status(HttpStatus.OK).body(category.get());
    }

    @PutMapping("/category")
    public ResponseEntity<Category> update(@RequestBody UpdateCategoryRequest request) throws IdInvalidException {
        Optional<Category> categoryInDB = this.categoryService.getCategory(request.getId());
        if(categoryInDB.isEmpty()) {
            throw new IdInvalidException("Category does not exist");
        }
        Category updatedCategory = this.categoryService.updateCategory(request, categoryInDB.get());
        return ResponseEntity.status(HttpStatus.OK).body(updatedCategory);
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Category> categoryInDB = this.categoryService.getCategory(id);
        if(categoryInDB.isEmpty()) {
            throw new IdInvalidException("Category does not exist");
        }
        this.categoryService.delete(categoryInDB.get());
        return ResponseEntity.status(HttpStatus.OK).body("Delete Success");
    }

    @GetMapping("/category/account-category")
    public ResponseEntity<List<ResCategoryDTO>> getCategoryAccount() {
        List<ResCategoryDTO> res = this.categoryService.getCategoriesByCategoryType(ETypeCategory.ACCOUNT);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/category/campaign-category")
    public ResponseEntity<List<ResCategoryDTO>> getCategoryCampaign() {
        List<ResCategoryDTO> res = this.categoryService.getCategoriesByCategoryType(ETypeCategory.CAMPAIGN);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
