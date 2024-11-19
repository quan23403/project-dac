package com.example.ProjectDAC.service;

import com.example.ProjectDAC.domain.Account;
import com.example.ProjectDAC.domain.Category;
import com.example.ProjectDAC.domain.CategoryBinding;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.repository.AccountRepository;
import com.example.ProjectDAC.repository.CategoryBindingRepository;
import com.example.ProjectDAC.request.CategoryBindingRequest;
import com.example.ProjectDAC.util.constant.ETypeCategory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryBindingService {
    private final CategoryBindingRepository categoryBindingRepository;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private final CampaignService campaignService;
    public CategoryBindingService(CategoryBindingRepository categoryBindingRepository, CategoryService categoryService, AccountService accountService,
                                  CampaignService campaignService) {
        this.categoryBindingRepository = categoryBindingRepository;
        this.categoryService = categoryService;
        this.accountService = accountService;
        this.campaignService = campaignService;
    }

    public CategoryBinding create(CategoryBindingRequest request) throws IdInvalidException {
        Optional<Category> category = this.categoryService.getCategory(request.getCategoryId());
        if (category.isEmpty()) {
            throw new IdInvalidException("Category does not exist");
        }
        if(request.getEntityType() == ETypeCategory.ACCOUNT) {
            if(!this.accountService.isExistedAccountById(request.getEntityId())) {
                throw new IdInvalidException("Account ID does not exist");
            }
            if(this.categoryBindingRepository.existsByEntityIdAndEntityType(request.getEntityId(), request.getEntityType())) {
                throw new IdInvalidException("Account is already linked");
            }
        }
        if(request.getEntityType() == ETypeCategory.CAMPAIGN) {
            if(!this.campaignService.isExistedCampaignById(request.getEntityId())) {
                throw new IdInvalidException("Campaign ID does not exist");
            }
            if(this.categoryBindingRepository.existsByEntityIdAndEntityType(request.getEntityId(), request.getEntityType())) {
                throw new IdInvalidException("Campaign is already linked");
            }
        }
        CategoryBinding categoryBinding = new CategoryBinding();
        categoryBinding.setCategory(category.get());
        categoryBinding.setEntityId(request.getEntityId());
        categoryBinding.setEntityType(request.getEntityType());
        return this.categoryBindingRepository.save(categoryBinding);
    }

    public void deleteCategoryAccount(long entityId, ETypeCategory entityType) throws IdInvalidException {
        CategoryBinding categoryBinding = this.categoryBindingRepository.findByEntityIdAndEntityType(entityId, entityType);
        if(categoryBinding == null) {
            throw new IdInvalidException("Account-Category not found");
        }
        this.categoryBindingRepository.deleteById(categoryBinding.getId());
    }

    public CategoryBinding update(CategoryBindingRequest request) throws IdInvalidException {
        CategoryBinding categoryBinding = this.categoryBindingRepository.findByEntityIdAndEntityType(request.getEntityId(), request.getEntityType());
        Optional<Category> category = this.categoryService.getCategory(request.getCategoryId());
        if(category.isEmpty()) {
            throw new IdInvalidException("Category does not exist");
        }
        categoryBinding.setCategory(category.get());
        return this.categoryBindingRepository.save(categoryBinding);
    }
}
