package com.example.ProjectDAC.service;

import com.example.ProjectDAC.domain.Anken;
import com.example.ProjectDAC.domain.Category;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.repository.AnkenRepository;
import com.example.ProjectDAC.repository.CategoryRepository;
import com.example.ProjectDAC.request.CreateCategoryRequest;
import com.example.ProjectDAC.request.UpdateCategoryRequest;
import com.example.ProjectDAC.util.constant.EStatus;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final AnkenRepository ankenRepository;
    private final AnkenService ankenService;
    public CategoryService(CategoryRepository categoryRepository, AnkenRepository ankenRepository, AnkenService ankenService) {
        this.categoryRepository = categoryRepository;
        this.ankenRepository = ankenRepository;
        this.ankenService = ankenService;
    }
    public Category create(CreateCategoryRequest request) throws IdInvalidException {
        Category category = new Category();
        if(request.getNameAnken() != null) {
            Anken anken = this.ankenRepository.findByName(request.getNameAnken());
            if(anken == null) {
                Anken newAnken = new Anken();
                newAnken.setName(request.getNameAnken());
                anken = this.ankenService.create(newAnken);
            }
            category.setAnken(anken);
        }
        category.setName(request.getName());
        category.setTypeCategory(request.getTypeCategory());
        category.setBudget(request.getBudget());
        category.setKpiGoal(request.getKpiGoal());
        category.setKpiType(request.getKpiType());
        category.setStartDate(request.getStartDate());
        category.setEndDate(request.getEndDate());

        return this.categoryRepository.save(category);
    }

    public boolean isExistedCategoryByName(String name) {
        return this.categoryRepository.existsByName(name);
    }

    public boolean isExistedCategoryById(long id) {
        return this.categoryRepository.existsById(id);
    }
    public List<Category> getCategories() {
        return this.categoryRepository.findByStatus(EStatus.ACTIVE);
    }

    public Optional<Category> getCategory(long id) {
        return this.categoryRepository.findById(id);
    }

    public Category getCategoryByName(String name) {
        return this.categoryRepository.findByName(name);
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

    @Transactional
    public void deleteCategoryByName(String name) {
        this.categoryRepository.deleteByName(name);
    }

    public Category updateCategoryByName(UpdateCategoryRequest updateCategory) {
        Category categoryInDB = categoryRepository.findByName(updateCategory.getName());
//        if(categoryInDB.getTypeCategory() != updateCategory.getTypeCategory()) {
//            return null;
//        }
        Anken anken = this.ankenRepository.findByName(updateCategory.getNameAnken());
        if(anken == null) {
            Anken newAnken = new Anken();
            newAnken.setName(updateCategory.getNameAnken());
            anken = this.ankenService.create(newAnken);
        }
        categoryInDB.setAnken(anken);
        categoryInDB.setBudget(updateCategory.getBudget());
        categoryInDB.setKpiType(updateCategory.getKpiType());
        categoryInDB.setKpiGoal(updateCategory.getKpiGoal());
        categoryInDB.setStartDate(updateCategory.getStartDate());
        categoryInDB.setEndDate(updateCategory.getEndDate());

        return this.categoryRepository.save(categoryInDB);
    }

}
