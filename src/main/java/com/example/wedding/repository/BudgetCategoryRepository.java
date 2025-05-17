package com.example.wedding.repository;

import com.example.wedding.model.BudgetCategory;
import com.example.wedding.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory, Integer> {
    @Query("SELECT DISTINCT bc FROM BudgetCategory bc LEFT JOIN FETCH bc.expenses WHERE bc.project = :project")
    List<BudgetCategory> findByProject(Project project);
} 