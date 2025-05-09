package com.example.wedding.repository;

import com.example.wedding.model.BudgetCategory;
import com.example.wedding.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory, Integer> {
    List<BudgetCategory> findByProject(Project project);
} 