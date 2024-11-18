package com.example.ProjectDAC.repository;

import com.example.ProjectDAC.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryBindingRepository extends JpaRepository<Category, Long> {
}
