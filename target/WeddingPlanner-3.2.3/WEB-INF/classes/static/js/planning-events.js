function sendTaskToEntourage(button) {
    const projectIdInput = document.querySelector('input[name="projectId"]');
    const projectId = projectIdInput ? projectIdInput.value : null;

    if (window.location.pathname.includes('/project/') && !projectId) {
        alert("Please create or select a project first.");
        return;
    }

    if (button.classList.contains('loading')) {
        return;
    }

    button.classList.add('loading');
    const originalText = button.textContent;
    button.textContent = 'Sending...';

    const taskId = button.getAttribute('data-task-id');
    const entourageType = button.getAttribute('data-entourage');

    // Determine the URL based on whether we're in admin or user mode
    let url;
    if (window.location.pathname.includes('/project/')) {
        url = `/project/${projectId}/task/${taskId}/send-email`;
    } else {
        url = `/task/${taskId}/send-email`;
    }

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `entourageType=${encodeURIComponent(entourageType)}`
    })
    .then(response => response.text())
    .then(result => {
        alert(result);
    })
    .catch(error => {
        alert('Error sending task email: ' + error);
    })
    .finally(() => {
        button.classList.remove('loading');
        button.textContent = originalText;
    });
}

function sendItineraryToEntourage(button) {
    const projectIdInput = document.querySelector('input[name="projectId"]');
    const projectId = projectIdInput ? projectIdInput.value : null;

    if (window.location.pathname.includes('/project/') && !projectId) {
        alert("Please create or select a project first.");
        return;
    }
    
    if (button.classList.contains('loading')) {
        return;
    }

    button.classList.add('loading');
    const originalText = button.textContent;
    button.textContent = 'Sending...';

    const entourageType = button.getAttribute('data-entourage');

    // Determine the URL based on whether we're in admin or user mode
    let url;
    if (window.location.pathname.includes('/project/')) {
        url = `/project/${projectId}/itinerary/send-email`;
    } else {
        url = `/itinerary/send-email`;
    }

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `entourageType=${encodeURIComponent(entourageType)}`
    })
    .then(response => response.text())
    .then(result => {
        alert(result);
    })
    .catch(error => {
        alert('Error sending itinerary email: ' + error);
    })
    .finally(() => {
        button.classList.remove('loading');
        button.textContent = originalText;
    });
}

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

// Document ready event handlers
document.addEventListener('DOMContentLoaded', function() {
    // Close dropdown when clicking outside
    document.addEventListener('click', function(event) {
        const dropdowns = document.querySelectorAll('.email-dropdown');
        dropdowns.forEach(dropdown => {
            if (!dropdown.contains(event.target)) {
                const content = dropdown.querySelector('.email-dropdown-content');
                if (content) {
                    content.style.display = 'none';
                }
            }
        });
    });

    // Toggle dropdown
    document.querySelectorAll('.email-btn').forEach(button => {
        button.addEventListener('click', function(event) {
            event.stopPropagation();
            const dropdown = this.nextElementSibling;
            dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
        });
    });

    // Close modals when clicking outside
    window.onclick = function(event) {
        if (event.target.className === 'modal') {
            event.target.style.display = 'none';
        }
    };
}); 