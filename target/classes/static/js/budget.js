function openBudgetModal() {
    document.getElementById('budgetModal').style.display = 'block';
}

function closeBudgetModal() {
    document.getElementById('budgetModal').style.display = 'none';
}

function openBudgetCategoryModal() {
    document.getElementById('budgetCategoryModal').style.display = 'block';
}

function closeBudgetCategoryModal() {
    document.getElementById('budgetCategoryModal').style.display = 'none';
}

function openEditCategoryModal(categoryId, name, budget, description) {
    const modal = document.getElementById('editCategoryModal');
    
    // Fill the form with category data
    document.getElementById('editCategoryId').value = categoryId;
    document.getElementById('editCategoryName').value = name;
    document.getElementById('editCategoryBudget').value = budget;
    document.getElementById('editCategoryDescription').value = description || '';
    
    modal.style.display = 'block';
}

function closeEditCategoryModal() {
    document.getElementById('editCategoryModal').style.display = 'none';
}

// Close modals when clicking outside
window.onclick = function(event) {
    if (event.target.className === 'modal') {
        event.target.style.display = 'none';
    }
}

// Edit expense button functionality
document.addEventListener('DOMContentLoaded', function() {
    // Setup expense edit buttons
    document.querySelectorAll('.expense-item .edit-btn').forEach(button => {
        button.addEventListener('click', function() {
            const expenseId = this.getAttribute('data-expense-id');
            // Implementation for editing expense would go here
            console.log(`Edit expense with ID: ${expenseId}`);
            // This would typically open a modal with the expense data pre-filled
        });
    });

    // Setup expense delete buttons
    document.querySelectorAll('.expense-item .delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            if (confirm('Are you sure you want to delete this expense?')) {
                const expenseId = this.getAttribute('data-expense-id');
                const projectId = document.querySelector('input[name="projectId"]').value;
                
                // Check if we're in admin or user mode
                let url;
                if (window.location.pathname.includes('/project/')) {
                    url = `/project/${projectId}/budget/expense/${expenseId}/delete`;
                } else {
                    url = `/budget/expense/${expenseId}/delete`;
                }
                
                // Send delete request
                fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                })
                .then(response => {
                    if (response.ok) {
                        window.location.reload();
                    } else {
                        alert('Error deleting expense');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Error deleting expense');
                });
            }
        });
    });

    // Setup category edit buttons
    document.querySelectorAll('.category-actions .edit-btn').forEach(button => {
        button.addEventListener('click', function() {
            const categoryId = this.getAttribute('data-category-id');
            const name = this.getAttribute('data-name');
            const budget = this.getAttribute('data-budget');
            const description = this.getAttribute('data-description');
            
            openEditCategoryModal(categoryId, name, budget, description);
        });
    });
    
    // Setup category delete buttons
    document.querySelectorAll('.category-actions .delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            if (confirm('Are you sure you want to delete this category? All expenses in this category will also be deleted.')) {
                const categoryId = this.getAttribute('data-category-id');
                const projectId = document.querySelector('input[name="projectId"]').value;
                
                // Check if we're in admin or user mode
                let url;
                if (window.location.pathname.includes('/project/')) {
                    url = `/project/${projectId}/budget/category/${categoryId}/delete`;
                } else {
                    url = `/budget/category/${categoryId}/delete`;
                }
                
                // Send delete request
                fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                })
                .then(response => {
                    if (response.ok) {
                        window.location.reload();
                    } else {
                        alert('Error deleting category');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Error deleting category');
                });
            }
        });
    });
}); 