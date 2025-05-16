function initializeBudgetChart(budgetCategories, totalBudget) {
    if (!budgetCategories || budgetCategories.length === 0) {
        return;
    }

    const ctx = document.getElementById('budgetPieChart');
    if (!ctx) {
        console.error('Budget chart canvas not found');
        return;
    }

    const labels = budgetCategories.map(category => category.name);
    const data = budgetCategories.map(category => category.budget);
    const backgroundColors = [
        '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF',
        '#FF9F40', '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0'
    ];

    new Chart(ctx, {
        type: 'pie',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: backgroundColors,
                borderWidth: 1
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
                            size: 12
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const value = context.raw;
                            const percentage = ((value / totalBudget) * 100).toFixed(1);
                            return `${context.label}: ₱${value.toLocaleString()} (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });
} 