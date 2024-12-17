package com.example.ProjectDAC.service;

import com.example.ProjectDAC.domain.Anken;
import com.example.ProjectDAC.domain.Category;
import com.example.ProjectDAC.domain.dto.ResCategoryDTO;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.repository.AnkenRepository;
import com.example.ProjectDAC.repository.CategoryRepository;
import com.example.ProjectDAC.request.CreateCategoryRequest;
import com.example.ProjectDAC.request.UpdateCategoryRequest;
import com.example.ProjectDAC.util.JwtUtils;
import com.example.ProjectDAC.util.constant.EStatus;
import com.example.ProjectDAC.util.constant.ETypeCategory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final AnkenRepository ankenRepository;
    private final AnkenService ankenService;
    private final JwtUtils jwtUtils;
    public CategoryService(CategoryRepository categoryRepository, AnkenRepository ankenRepository, AnkenService ankenService, JwtUtils jwtUtils) {
        this.categoryRepository = categoryRepository;
        this.ankenRepository = ankenRepository;
        this.ankenService = ankenService;
        this.jwtUtils = jwtUtils;
    }
    public Category create(CreateCategoryRequest request) throws IdInvalidException {
        Category category = new Category();
        if(request.getAnkenName() != null) {
            Anken anken = this.ankenRepository.findByName(request.getAnkenName());
            if(anken == null) {
                Anken newAnken = new Anken();
                newAnken.setName(request.getAnkenName());
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

    public ResCategoryDTO convertCategory(Category category) {
        ResCategoryDTO res = new ResCategoryDTO();
        res.setId(category.getId());
        res.setName(category.getName());
        res.setBudget(category.getBudget());
        res.setStartDate(category.getStartDate());
        res.setEndDate(category.getEndDate());
        res.setTypeCategory(category.getTypeCategory());
        res.setKpiGoal(category.getKpiGoal());
        res.setKpiType(category.getKpiType());
        if(category.getAnken() != null) {
            res.setAnkenId(category.getAnken().getId());
            res.setAnkenName(category.getAnken().getName());
        }
        return res;
    }
    public List<ResCategoryDTO> getCategories(List<Long> ids) {
        return this.categoryRepository.findByStatusAndAnkenIdIn(EStatus.ACTIVE, ids)
                .stream().map(this::convertCategory)
                .collect(Collectors.toList());
    }

    public List<ResCategoryDTO> getCategoriesByCategoryType(ETypeCategory typeCategory) {
        return this.categoryRepository.findByStatusAndTypeCategory(EStatus.ACTIVE, typeCategory)
                .stream().map(this::convertCategory)
                .collect(Collectors.toList());
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
        if(request.getAnkenName() != null) {
            Anken anken = this.ankenRepository.findByName(request.getAnkenName());
            if(anken == null) {
                Anken newAnken = new Anken();
                newAnken.setName(request.getAnkenName());
                anken = this.ankenService.create(newAnken);
            }
            category.setAnken(anken);
        }
        return this.categoryRepository.save(category);
    }

    public void delete(Category category) {
        category.setAnken(null);
        this.categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategoryByName(String name) {
        Category category = this.categoryRepository.findByName(name);
        category.setAnken(null);
        this.categoryRepository.save(category);
    }

    public void updateCategoryByName(UpdateCategoryRequest updateCategory) {
        Category categoryInDB = categoryRepository.findByName(updateCategory.getName());
//        if(categoryInDB.getTypeCategory() != updateCategory.getTypeCategory()) {
//            return null;
//        }
        Anken anken = this.ankenRepository.findByName(updateCategory.getAnkenName());
        if(anken == null) {
            Anken newAnken = new Anken();
            newAnken.setName(updateCategory.getAnkenName());
            anken = this.ankenService.create(newAnken);
        }
        categoryInDB.setAnken(anken);
        categoryInDB.setBudget(updateCategory.getBudget());
        categoryInDB.setKpiType(updateCategory.getKpiType());
        categoryInDB.setKpiGoal(updateCategory.getKpiGoal());
        categoryInDB.setStartDate(updateCategory.getStartDate());
        categoryInDB.setEndDate(updateCategory.getEndDate());

        this.categoryRepository.save(categoryInDB);
    }

    public boolean checkIdIn(long ankenId, List<Long> ids) {
        return ids != null && ids.contains(ankenId);
    }

    public void checkPermission(HttpServletRequest request, Category category) throws IdInvalidException {
        String token = request.getHeader("Authorization");
        List<Long> ids = this.jwtUtils.getAnkenListFromToken(token);
        if(!this.checkIdIn(category.getAnken().getId(), ids)) {
            throw new IdInvalidException("You do not have permission to get this Category");
        }
    }
}
