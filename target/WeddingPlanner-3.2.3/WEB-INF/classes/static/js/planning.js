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
    // Task checkbox functionality
    const checkboxes = document.querySelectorAll('.task-checkbox');
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const taskId = this.getAttribute('data-task-id');
            const isCompleted = this.checked;

            // Get the project ID from the hidden input field
            const projectIdInput = document.querySelector('input[name="projectId"]');
            const projectId = projectIdInput ? projectIdInput.value : null;
            
            // Determine the URL based on whether we're in admin or user mode
            let url;
            if (window.location.pathname.includes('/project/')) {
                url = `/project/${projectId}/task/${taskId}/update-status`;
            } else {
                url = `/task/${taskId}/update-status`;
            }

            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `completed=${isCompleted}`
            })
            .then(response => {
                if (response.ok) {
                    // Update the UI to reflect the task status
                    const taskTitle = this.nextElementSibling;
                    if (isCompleted) {
                        taskTitle.classList.add('completed');
                    } else {
                        taskTitle.classList.remove('completed');
                    }
                } else {
                    console.error('Failed to update task status');
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
        });
    });

    // Delete task functionality
    document.querySelectorAll('.task-item .delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            if (confirm('Are you sure you want to delete this task?')) {
                const taskId = this.getAttribute('data-task-id');
                
                // Get the project ID from the hidden input field
                const projectIdInput = document.querySelector('input[name="projectId"]');
                const projectId = projectIdInput ? projectIdInput.value : null;
                
                // Determine the URL based on whether we're in admin or user mode
                let url;
                if (window.location.pathname.includes('/project/')) {
                    url = `/project/${projectId}/task/${taskId}/delete`;
                } else {
                    url = `/task/${taskId}/delete`;
                }

                fetch(url, {
                    method: 'POST'
                })
                .then(response => {
                    if (response.ok) {
                        // Remove the task from the UI
                        this.closest('.task-item').remove();
                    } else {
                        console.error('Failed to delete task');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                });
            }
        });
    });

    // Delete itinerary item functionality
    document.querySelectorAll('.itinerary-item .delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            if (confirm('Are you sure you want to delete this event?')) {
                const itemId = this.getAttribute('data-item-id');
                
                // Get the project ID from the hidden input field
                const projectIdInput = document.querySelector('input[name="projectId"]');
                const projectId = projectIdInput ? projectIdInput.value : null;
                
                // Determine the URL based on whether we're in admin or user mode
                let url;
                if (window.location.pathname.includes('/project/')) {
                    url = `/project/${projectId}/itinerary/${itemId}/delete`;
                } else {
                    url = `/itinerary/${itemId}/delete`;
                }

                fetch(url, {
                    method: 'POST'
                })
                .then(response => {
                    if (response.ok) {
                        // Remove the itinerary item from the UI
                        this.closest('.itinerary-item').remove();
                    } else {
                        console.error('Failed to delete itinerary item');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
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