document.addEventListener('DOMContentLoaded', function() {
    // Initialize budget chart
    const budgetChart = document.getElementById('budgetPieChart');
    if (budgetChart) {
        const categories = document.querySelectorAll('.budget-category');
        if (categories && categories.length > 0) {
            const labels = [];
            const data = [];
            
            categories.forEach(category => {
                const nameElement = category.querySelector('.category-name');
                const amountElement = category.querySelector('.budget-amount');
                
                if (nameElement && amountElement) {
                    const name = nameElement.textContent;
                    const amount = amountElement.textContent
                        .replace('Budget: ₱', '')
                        .replace(/,/g, '');
                    
                    if (name && amount) {
                        labels.push(name);
                        data.push(parseFloat(amount));
                    }
                }
            });

            if (labels.length > 0 && data.length > 0) {
                new Chart(budgetChart, {
                    type: 'pie',
                    data: {
                        labels: labels,
                        datasets: [{
                            data: data,
                            backgroundColor: [
                                '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', 
                                '#9966FF', '#FF9F40', '#8AC249', '#EA526F',
                                '#23B5D3', '#279AF1'
                            ]
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'right',
                                labels: {
                                    font: {
                                        size: 14
                                    }
                                }
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        const value = context.raw;
                                        const total = data.reduce((a, b) => a + b, 0);
                                        const percentage = ((value / total) * 100).toFixed(1);
                                        return `${context.label}: ₱${value.toLocaleString()} (${percentage}%)`;
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    // Only disable elements if project is completed
    const completedMessage = document.querySelector('.completed-message');
    if (completedMessage) {
        const header = document.querySelector('header');
        const allElements = document.querySelectorAll('input, button, select, textarea, a:not([href^="/"]), [onclick]');
        
        allElements.forEach(element => {
            // Skip elements in the header
            if (!header.contains(element)) {
                if (element.tagName === 'INPUT' || element.tagName === 'SELECT' || element.tagName === 'TEXTAREA') {
                    element.disabled = true;
                } else if (element.tagName === 'BUTTON' || element.tagName === 'A') {
                    element.style.pointerEvents = 'none';
                    element.style.opacity = '0.6';
                    element.style.cursor = 'not-allowed';
                }
                // Remove onclick handlers
                element.removeAttribute('onclick');
            }
        });
    }
});

function toggleTask(checkbox) {
    const taskId = checkbox.dataset.taskId;
    const completed = checkbox.checked;
    
    fetch(`/project/${taskId}/task/toggle`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ completed: completed })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            if (completed) {
                checkbox.closest('li').remove();
            }
        } else {
            checkbox.checked = !completed;
            alert('Failed to update task status');
        }
    })
    .catch(error => {
        checkbox.checked = !completed;
        alert('Error updating task status');
    });
} 