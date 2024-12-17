package com.example.ProjectDAC.repository;

import com.example.ProjectDAC.domain.Category;
import com.example.ProjectDAC.domain.CategoryBinding;
import com.example.ProjectDAC.domain.dto.AccountCategoryDTO;
import com.example.ProjectDAC.util.constant.ETypeCategory;
import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryBindingRepository extends JpaRepository<CategoryBinding, Long> {
    boolean existsByEntityIdAndEntityType(long id, ETypeCategory entityType);

    CategoryBinding findByEntityIdAndEntityType(long id, ETypeCategory entityType);

    @Query(value = "SELECT anken.name AS ankenName, a.id AS accountId , a.name AS accountName, a.account_code AS accountCode, a.media AS media, cb.category_id As categoryId,\n" +
            "\tcb.name AS categoryName\n" +
            "FROM account AS a\n" +
            "LEFT JOIN (Select entity_id, entity_type, category_id, name from category_binding\n" +
            "JOIN category c ON c.id = category_binding.category_id\n" +
            "where category_binding.entity_type = 'ACCOUNT') As cb ON cb.entity_id = a.id\n" +
            "JOIN anken ON anken.id = a.anken_id;",
            nativeQuery = true)
    List<Object[]> findAccountCategoryDetailsRaw();

    @Query(value = "SELECT anken.name AS ankenName, a.name AS accountName, a.account_code AS accountCode, a.media AS media, \n" +
            "       cam.id AS campaignId, cam.name AS campaignName, cam.campaign_code AS campaignCode, cb.category_id AS categoryId, cb.name AS categoryName\n" +
            "FROM campaign AS cam\n" +
            "JOIN account a ON cam.account_id = a.id\n" +
            "LEFT JOIN (Select entity_id, entity_type, category_id, name from category_binding\n" +
            "JOIN category c ON c.id = category_binding.category_id\n" +
            "where category_binding.entity_type = 'CAMPAIGN') As cb ON cb.entity_id = cam.id\n" +
            "JOIN anken ON anken.id = a.anken_id;",
            nativeQuery = true)
    List<Object[]> findCampaignCategoryDetailsRaw();
}
