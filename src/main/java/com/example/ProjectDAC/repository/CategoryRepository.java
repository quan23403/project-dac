package com.example.ProjectDAC.repository;

import com.example.ProjectDAC.domain.Category;
import com.example.ProjectDAC.util.constant.EStatus;
import com.example.ProjectDAC.util.constant.ETypeCategory;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);


    List<Category> findByStatusAndAnkenIdIn(EStatus status, List<Long> ids);

    @Query("select count(c) from Category c where c.name = ?1 and c.id != ?2")
    long checkNameCategory(String email, long id);

    void deleteByName(String name);

    Category findByName(@NotBlank(message = "Name Category khong duoc de trong") String name);

    List<Category> findByStatusAndTypeCategory(EStatus eStatus, ETypeCategory typeCategory);
}
