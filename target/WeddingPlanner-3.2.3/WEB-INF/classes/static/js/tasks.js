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