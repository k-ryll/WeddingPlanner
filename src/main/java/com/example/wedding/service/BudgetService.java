package com.example.wedding.service;

import com.example.wedding.model.BudgetCategory;
import com.example.wedding.model.Expense;
import com.example.wedding.model.Project;
import com.example.wedding.repository.BudgetCategoryRepository;
import com.example.wedding.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class BudgetService {
    @Autowired
    private BudgetCategoryRepository budgetCategoryRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    public List<BudgetCategory> findByProject(Project project) {
        return budgetCategoryRepository.findByProject(project);
    }

    public BudgetCategory saveCategory(BudgetCategory category) {
        return budgetCategoryRepository.save(category);
    }

    public void deleteCategory(Integer id) {
        budgetCategoryRepository.deleteById(id);
    }

    public Expense saveExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public void deleteExpense(Integer id) {
        expenseRepository.deleteById(id);
    }

    public BigDecimal getTotalBudget(Project project) {
        return budgetCategoryRepository.findByProject(project).stream()
                .map(BudgetCategory::getBudget)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalSpent(Project project) {
        return budgetCategoryRepository.findByProject(project).stream()
                .map(BudgetCategory::getTotalSpent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
} 