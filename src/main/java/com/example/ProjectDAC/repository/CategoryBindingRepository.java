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

    @Query(value = "SELECT anken.name AS ankenName, a.id AS accountId, a.name AS accountName, a.account_code AS accountCode, a.media AS media, " +
            "c.id AS categoryId, c.name AS categoryName " +
            "FROM (SELECT * FROM project_dac.category_binding WHERE entity_type = 'ACCOUNT') AS temp_table " +
            "JOIN account a ON temp_table.entity_id = a.id " +
            "JOIN category c ON temp_table.category_id = c.id " +
            "JOIN anken ON anken.id = a.id",
            nativeQuery = true)
    List<Object[]> findAccountCategoryDetailsRaw();

    @Query(value = "SELECT anken.name AS ankenName, a.name AS accountName, a.account_code AS accountCode, a.media AS media, " +
            "cam.id AS campaignId, cam.name AS campaignName, cam.campaign_code AS campaignCode, " +
            "c.id AS categoryId, c.name AS categoryName " +
            "FROM (SELECT * FROM project_dac.category_binding WHERE entity_type = 'CAMPAIGN') AS temp_table " +
            "JOIN campaign cam ON temp_table.entity_id = cam.id " +
            "JOIN account a ON a.id = cam.account_id " +
            "JOIN category c ON temp_table.category_id = c.id " +
            "JOIN anken ON anken.id = a.anken_id",
            nativeQuery = true)
    List<Object[]> findCampaignCategoryDetailsRaw();
}
