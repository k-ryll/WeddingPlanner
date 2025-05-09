package com.example.wedding.repository;

import com.example.wedding.model.Expense;
import com.example.wedding.model.BudgetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findByCategory(BudgetCategory category);
} 