package com.example.ProjectDAC.repository;

import com.example.ProjectDAC.domain.Category;
import com.example.ProjectDAC.domain.CategoryBinding;
import com.example.ProjectDAC.util.constant.ETypeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryBindingRepository extends JpaRepository<CategoryBinding, Long> {
    boolean existsByEntityIdAndEntityType(long id, ETypeCategory entityType);

    CategoryBinding findByEntityIdAndEntityType(long id, ETypeCategory entityType);
}
