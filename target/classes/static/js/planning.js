// Modal functions
function openTaskModal() {
    document.getElementById('taskModal').style.display = 'block';
}

function closeTaskModal() {
    document.getElementById('taskModal').style.display = 'none';
}

function openItineraryModal() {
    document.getElementById('itineraryModal').style.display = 'block';
}

function closeItineraryModal() {
    document.getElementById('itineraryModal').style.display = 'none';
}

// Close modals when clicking outside
window.onclick = function(event) {
    if (event.target.className === 'modal') {
        event.target.style.display = 'none';
    }
}

document.addEventListener('DOMContentLoaded', function() {
    // Task completion toggle
    document.querySelectorAll('.task-checkbox').forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const taskId = this.dataset.taskId;
            const completed = this.checked;
            
            fetch(`/project/${taskId}/task/toggle`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ completed: completed })
            })
            .then(response => response.json())
            .then(data => {
                if (!data.success) {
                    this.checked = !completed;
                    alert('Failed to update task status');
                }
            });
        });
    });

    // Task deletion
    document.querySelectorAll('.task-item .delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            if (confirm('Are you sure you want to delete this task?')) {
                const taskId = this.dataset.taskId;
                fetch(`/project/${taskId}/task/delete`, {
                    method: 'DELETE'
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        this.closest('.task-item').remove();
                    } else {
                        alert('Failed to delete task');
                    }
                });
            }
        });
    });

    // Itinerary item deletion
    document.querySelectorAll('.itinerary-item .delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            if (confirm('Are you sure you want to delete this event?')) {
                const itemId = this.dataset.itemId;
                fetch(`/project/${itemId}/itinerary/delete`, {
                    method: 'DELETE'
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        this.closest('.itinerary-item').remove();
                    } else {
                        alert('Failed to delete event');
                    }
                });
            }
        });
    });
});

function deleteBudgetCategory(categoryId) {
    if (confirm('Are you sure you want to delete this budget category? All expenses in this category will also be deleted.')) {
        const projectId = document.querySelector('input[name="projectId"]').value;
        fetch(`/project/${projectId}/budget/category/${categoryId}/delete`, {
            method: 'DELETE'
        })
        .then(response => response.json())
        .then(result => {
            if (result.success) {
                location.reload();
            } else {
                alert('Failed to delete category');
            }
        })
        .catch(error => {
            alert('Error deleting category: ' + error);
        });
    }
}

function deleteExpense(expenseId) {
    if (confirm('Are you sure you want to delete this expense?')) {
        const projectId = document.querySelector('input[name="projectId"]').value;
        fetch(`/project/${projectId}/budget/expense/${expenseId}/delete`, {
            method: 'DELETE'
        })
        .then(response => response.json())
        .then(result => {
            if (result.success) {
                location.reload();
            } else {
                alert('Failed to delete expense');
            }
        })
        .catch(error => {
            alert('Error deleting expense: ' + error);
        });
    }
}

function editExpense(expenseId) {
    // TODO: Implement edit expense functionality
    alert('Edit expense functionality coming soon!');
}

function editBudgetCategory(categoryId) {
    // TODO: Implement edit budget category functionality
    alert('Edit budget category functionality coming soon!');
} 