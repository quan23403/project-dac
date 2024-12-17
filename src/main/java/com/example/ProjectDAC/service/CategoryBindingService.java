package com.example.ProjectDAC.service;

import com.example.ProjectDAC.domain.Category;
import com.example.ProjectDAC.domain.CategoryBinding;
import com.example.ProjectDAC.domain.dto.AccountCategoryDTO;
import com.example.ProjectDAC.domain.dto.CampaignCategoryDTO;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.repository.CategoryBindingRepository;
import com.example.ProjectDAC.request.CategoryBindingRequest;
import com.example.ProjectDAC.util.constant.ETypeCategory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public CategoryBinding create(CategoryBindingRequest request, List<Long> ids) throws IdInvalidException {
        Optional<Category> category = this.categoryService.getCategory(request.getCategoryId());
        if (category.isEmpty()) {
            throw new IdInvalidException("Category does not exist");
        }
        if(category.get().getAnken() == null) {
            throw new IdInvalidException("You do not have permission with this Category");
        }

        long ankenId = category.get().getAnken().getId();
        if(ids == null || !ids.contains(ankenId)) {
            throw new IdInvalidException("You do not have permission with this Category");
        }
        if(request.getEntityType() == ETypeCategory.ACCOUNT) {
            if(!this.accountService.isExistedAccountById(request.getEntityId())) {
                throw new IdInvalidException("Account ID does not exist");
            }
            if(!this.accountService.checkPermission(request.getEntityId(), ids)) {
                throw new IdInvalidException("You do not have permission with this account");
            }
            if(this.categoryBindingRepository.existsByEntityIdAndEntityType(request.getEntityId(), request.getEntityType())) {
                return this.update(request);
            }
        }
        if(request.getEntityType() == ETypeCategory.CAMPAIGN) {
            if(!this.campaignService.isExistedCampaignById(request.getEntityId())) {
                throw new IdInvalidException("Campaign ID does not exist");
            }
            if(!this.campaignService.checkPermission(request.getEntityId(), ids)) {
                throw new IdInvalidException("You do not have permission with this campaign");
            }
            if(this.categoryBindingRepository.existsByEntityIdAndEntityType(request.getEntityId(), request.getEntityType())) {
                return this.update(request);
            }
        }
        CategoryBinding categoryBinding = new CategoryBinding();
        categoryBinding.setCategory(category.get());
        categoryBinding.setEntityId(request.getEntityId());
        categoryBinding.setEntityType(request.getEntityType());
        return this.categoryBindingRepository.save(categoryBinding);
    }

    public void deleteCategoryBinding(long entityId, ETypeCategory entityType, List<Long> ids) throws IdInvalidException {
        if(entityType == ETypeCategory.ACCOUNT) {
            if(!this.accountService.isExistedAccountById(entityId)) {
                throw new IdInvalidException("Account ID does not exist");
            }
            if(!this.accountService.checkPermission(entityId, ids)) {
                throw new IdInvalidException("You do not have permission with this account");
            }
        }
        if(entityType == ETypeCategory.CAMPAIGN) {
            if(!this.campaignService.isExistedCampaignById(entityId)) {
                throw new IdInvalidException("Campaign ID does not exist");
            }
            if(!this.campaignService.checkPermission(entityId, ids)) {
                throw new IdInvalidException("You do not have permission with this campaign");
            }
        }
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

    public List<AccountCategoryDTO> getAccountCategoryDetails(List<Long> ids) {
        List<Object[]> results = categoryBindingRepository.findAccountCategoryDetailsRaw(ids);
        List<AccountCategoryDTO> dtos = new ArrayList<>();

        for (Object[] result : results) {
            String ankenName = (String) result[0];
            Long accountId = ((Number) result[1]).longValue();
            String accountName = (String) result[2];
            String accountCode = (String) result[3];
            String media = (String) result[4];
            Long categoryId = (result[5] != null) ? ((Number) result[5]).longValue() : null;
            String categoryName = (String) result[6];
            AccountCategoryDTO dto = new AccountCategoryDTO(ankenName, accountId, accountName, accountCode, media, categoryId, categoryName);
            dtos.add(dto);
        }
        return dtos;
    }

    public List<CampaignCategoryDTO> getCampaignCategoryDetails(List<Long> ids) {
        List<Object[]> results = categoryBindingRepository.findCampaignCategoryDetailsRaw(ids);
        List<CampaignCategoryDTO> dtos = new ArrayList<>();

        for (Object[] result : results) {
            String ankenName = (String) result[0];
            String accountName = (String) result[1];
            String accountCode = (String) result[2];
            String media = (String) result[3];
            Long campaignId = ((Number) result[4]).longValue();
            String campaignName = (String) result[5];
            String campaignCode = (String) result[6];
            Long categoryId = (result[7] != null) ? ((Number) result[7]).longValue() : null;
            String categoryName = (String) result[8];

            CampaignCategoryDTO dto = new CampaignCategoryDTO(ankenName, accountName, accountCode, media,
                    campaignId, campaignName, campaignCode,
                    categoryId, categoryName);
            dtos.add(dto);
        }
        return dtos;
    }
}
