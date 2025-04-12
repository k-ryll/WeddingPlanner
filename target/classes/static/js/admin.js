document.addEventListener("DOMContentLoaded", function () {
    // Search functionality
    const searchInput = document.querySelector('.search-box input[type="text"]');
    const projectGrid = document.querySelector('.projects-grid');
    
    if (searchInput && projectGrid) {
        console.log('Search elements found');
        
        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase().trim();
            console.log('Searching for:', searchTerm);
            
            const projectCards = projectGrid.querySelectorAll('.project-card');
            console.log('Found project cards:', projectCards.length);
            
            projectCards.forEach(card => {
                try {
                    const projectName = card.querySelector('h3')?.textContent.toLowerCase().trim() || '';
                    const brideName = card.querySelector('.detail-item:nth-child(1) span')?.textContent.toLowerCase().trim() || '';
                    const groomName = card.querySelector('.detail-item:nth-child(2) span')?.textContent.toLowerCase().trim() || '';
                    const weddingDate = card.querySelector('.detail-item:nth-child(4) span')?.textContent.toLowerCase().trim() || '';
                    
                    console.log('Checking card:', {
                        projectName,
                        brideName,
                        groomName,
                        weddingDate
                    });
                    
                    if (projectName.includes(searchTerm) || 
                        brideName.includes(searchTerm) || 
                        groomName.includes(searchTerm) || 
                        weddingDate.includes(searchTerm)) {
                        card.style.display = '';
                    } else {
                        card.style.display = 'none';
                    }
                } catch (error) {
                    console.error('Error processing project card:', error);
                }
            });
        });
    } else {
        console.error('Search elements not found:', {
            searchInput: !!searchInput,
            projectGrid: !!projectGrid
        });
    }

    const modal = document.getElementById("projectModal");
    const openModalBtn = document.getElementById("openModal");
    const closeModalBtn = document.querySelector(".close");
    const projectForm = document.querySelector(".projectForm");
    let currentProjectId = null;

    openModalBtn.addEventListener("click", () => {
        modal.style.display = "block";
        currentProjectId = null;
        projectForm.reset();
        document.querySelector(".modal-title").textContent = "Create New Project";
    });

    closeModalBtn.addEventListener("click", () => {
        modal.style.display = "none";
    });

    window.addEventListener("click", (event) => {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });

    // Handle edit button clicks
    document.querySelectorAll(".edit-btn").forEach(button => {
        button.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();
            const projectId = button.getAttribute("data-project-id");
            const projectCard = button.closest(".project-card");
            
            // Get project details from the card
            const projectName = projectCard.querySelector("h3").textContent;
            const status = projectCard.querySelector(".status-badge").textContent;
            const weddingDate = projectCard.querySelector(".detail-item:last-child span:last-child").textContent;
            
            // Set form values
            document.querySelector(".modal-title").textContent = "Edit Project";
            projectForm.querySelector("[name='projectName']").value = projectName;
            projectForm.querySelector("[name='status']").value = status;
            projectForm.querySelector("[name='weddingDate']").value = weddingDate;
            
            currentProjectId = projectId;
            modal.style.display = "block";
        });
    });

    // Handle delete button clicks
    document.querySelectorAll(".delete-btn").forEach(button => {
        button.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();
            if (confirm("Are you sure you want to delete this project?")) {
                const projectId = button.getAttribute("data-project-id");
                fetch(`/project/${projectId}/delete`, {
                    method: 'DELETE'
                })
                .then(response => {
                    if (response.ok) {
                        button.closest('.project-card').remove();
                    } else {
                        alert('Failed to delete project');
                    }
                });
            }
        });
    });

    // Handle form submission
    projectForm.addEventListener("submit", (e) => {
        e.preventDefault();
        
        const formData = new FormData(projectForm);
        if (currentProjectId) {
            formData.append("projectId", currentProjectId);
        }
        
        const form = document.createElement("form");
        form.method = "POST";
        form.action = currentProjectId ? "/project/edit" : "/project/create";
        
        for (let [key, value] of formData.entries()) {
            const input = document.createElement("input");
            input.type = "hidden";
            input.name = key;
            input.value = value;
            form.appendChild(input);
        }
        
        document.body.appendChild(form);
        form.submit();
    });
});
