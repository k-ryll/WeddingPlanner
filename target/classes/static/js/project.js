document.addEventListener('DOMContentLoaded', function() {
    // Handle save button clicks
    document.querySelectorAll('.save-btn').forEach(button => {
        button.addEventListener('click', function() {
            const guestId = this.dataset.guestId;
            const row = this.closest('tr');
            const rsvpStatus = row.querySelector('select').value;
            const remarks = row.querySelector('input[type="text"]').value;
            
            // Send update to server
            fetch(`/guestAdmin/${guestId}/update`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    rsvpStatus: rsvpStatus,
                    remarks: remarks
                })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Guest information updated successfully!');
                } else {
                    alert('Failed to update guest information.');
                }
            });
        });
    });

    // Handle delete button clicks
    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            if (confirm('Are you sure you want to delete this guest?')) {
                const guestId = this.dataset.guestId;
                fetch(`/guestAdmin/${guestId}/delete`, {
                    method: 'DELETE'
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        this.closest('tr').remove();
                        alert('Guest deleted successfully!');
                    } else {
                        alert('Failed to delete guest.');
                    }
                });
            }
        });
    });
});

function toggleTask(checkbox) {
    const taskId = checkbox.dataset.taskId;
    const completed = checkbox.checked;
    
    fetch(`/project/${taskId}/task/toggle?completed=${completed}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
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