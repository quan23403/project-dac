package com.example.ProjectDAC.service;

import com.example.ProjectDAC.domain.Category;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.repository.CategoryRepository;
import com.example.ProjectDAC.request.UpdateCategoryRequest;
import com.example.ProjectDAC.util.constant.EStatus;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    public Category create(@Valid @RequestBody Category category) {
        return this.categoryRepository.save(category);
    }

    public boolean isExistCategory(String name) {
        return this.categoryRepository.existsByName(name);
    }

    public List<Category> getCategories() {
        return this.categoryRepository.findByStatus(EStatus.ACTIVE);
    }

    public Optional<Category> getCategory(long id) {
        return this.categoryRepository.findById(id);
    }

    public Category updateCategory(UpdateCategoryRequest request, Category category) throws IdInvalidException {

        if(this.categoryRepository.checkNameCategory(request.getName(), request.getId()) > 0) {
            throw new IdInvalidException("Category's Name already exists");
        }
        category.setName(request.getName());
        category.setBudget(request.getBudget());
        category.setStartDate(request.getStartDate());
        category.setEndDate(request.getEndDate());
        category.setKpiType(request.getKpiType());
        category.setKpiGoal(request.getKpiGoal());
        return this.categoryRepository.save(category);
    }

    public void delete(Category category) {
        category.setStatus(EStatus.DELETED);
        this.categoryRepository.save(category);
    }

    public void deleteCategoryByName(String name) {
        this.categoryRepository.deleteByName(name);
    }

    public Category updateCategoryByName(Category updateCategory) {
        Category categoryInDB = categoryRepository.findByName(updateCategory.getName());
        if(categoryInDB.getTypeCategory() != updateCategory.getTypeCategory()) {
            return null;
        }
        categoryInDB.setBudget(updateCategory.getBudget());
        categoryInDB.setKpiType(updateCategory.getKpiType());
        categoryInDB.setKpiGoal(updateCategory.getKpiGoal());
        categoryInDB.setStartDate(updateCategory.getStartDate());
        categoryInDB.setEndDate(updateCategory.getEndDate());
        return this.categoryRepository.save(categoryInDB);
    }

}
