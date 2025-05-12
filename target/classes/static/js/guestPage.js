document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("guestModal");
    const openModalBtn = document.getElementById("openModal");
    const closeModalBtn = document.querySelector(".close");
    const guestForm = document.querySelector(".guestForm");
    let currentGuestId = null;

    openModalBtn.addEventListener("click", () => {
        modal.style.display = "block";
        currentGuestId = null;
        guestForm.reset();
        document.querySelector(".modal-title").textContent = "Add Guest";
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
            const guestId = button.getAttribute("data-guest-id");
            const row = button.closest("tr");
            
            // Get guest details from the row - corrected order based on table structure
            const title = row.cells[0].textContent.trim();
            const name = row.cells[1].textContent.trim();
            const email = row.cells[2].textContent.trim();
            const rsvp = row.cells[3].textContent.trim(); // Correct index for RSVP
            const entourage = row.cells[4].textContent.trim(); // Correct index for Entourage
            const remarks = row.cells[5].textContent.trim(); // Added Remarks
            
            // Set form values
            document.querySelector(".modal-title").textContent = "Edit Guest";
            
            // Set dropdown values properly
            const titleSelect = guestForm.querySelector("[name='title']");
            const entourageSelect = guestForm.querySelector("[name='entourage']");
            const rsvpSelect = guestForm.querySelector("[name='rsvp']");
            const remarksSelect = guestForm.querySelector("[name='remarks']");
            
            // Set text input values
            guestForm.querySelector("[name='name']").value = name;
            guestForm.querySelector("[name='email']").value = email;
            
            // Handle dropdowns by trying to find and select matching options
            setSelectValue(titleSelect, title);
            setSelectValue(entourageSelect, entourage);
            setSelectValue(rsvpSelect, rsvp);
            setSelectValue(remarksSelect, remarks);
            
            currentGuestId = guestId;
            modal.style.display = "block";
        });
    });

    // Helper function to set select element values properly
    function setSelectValue(selectElement, value) {
        if (!selectElement || !value) {
            console.log("Cannot set value - element or value is missing", {
                element: selectElement ? selectElement.name : "null",
                value: value
            });
            return;
        }
        
        console.log(`Trying to set ${selectElement.name} to "${value}"`);
        
        // Special case for RSVP to handle common mapping issues
        if (selectElement.name === "rsvp" || selectElement.id === "rsvp") {
            // Standardize common RSVP values
            const rsvpValue = value.trim().toLowerCase();
            if (rsvpValue === "accepted" || rsvpValue === "accept" || rsvpValue === "yes") {
                for (let i = 0; i < selectElement.options.length; i++) {
                    if (selectElement.options[i].value.toLowerCase() === "accepted") {
                        selectElement.selectedIndex = i;
                        console.log(`Set ${selectElement.name} to "Accepted" (index ${i})`);
                        return;
                    }
                }
            } else if (rsvpValue === "pending" || rsvpValue === "awaiting" || rsvpValue === "maybe") {
                for (let i = 0; i < selectElement.options.length; i++) {
                    if (selectElement.options[i].value.toLowerCase() === "pending") {
                        selectElement.selectedIndex = i;
                        console.log(`Set ${selectElement.name} to "Pending" (index ${i})`);
                        return;
                    }
                }
            } else if (rsvpValue === "declined" || rsvpValue === "decline" || rsvpValue === "no") {
                for (let i = 0; i < selectElement.options.length; i++) {
                    if (selectElement.options[i].value.toLowerCase() === "declined") {
                        selectElement.selectedIndex = i;
                        console.log(`Set ${selectElement.name} to "Declined" (index ${i})`);
                        return;
                    }
                }
            }
        }
        
        // Try to find an option that matches exactly
        let found = false;
        for (let i = 0; i < selectElement.options.length; i++) {
            const optionText = selectElement.options[i].text.trim();
            const optionValue = selectElement.options[i].value;
            
            console.log(`Option ${i}: text="${optionText}", value="${optionValue}"`);
            
            if (optionText === value || optionValue === value) {
                selectElement.selectedIndex = i;
                console.log(`Exact match found at index ${i}`);
                found = true;
                break;
            }
        }
        
        // If not found, try case-insensitive match
        if (!found) {
            const lowerValue = value.toLowerCase();
            for (let i = 0; i < selectElement.options.length; i++) {
                if (selectElement.options[i].text.trim().toLowerCase() === lowerValue || 
                    selectElement.options[i].value.toLowerCase() === lowerValue) {
                    selectElement.selectedIndex = i;
                    console.log(`Case-insensitive match found at index ${i}`);
                    break;
                }
            }
        }
        
        // Set default value if still not found and it's the RSVP field
        if (selectElement.name === "rsvp" && selectElement.selectedIndex === 0) {
            // Force "Pending" as default if available
            for (let i = 0; i < selectElement.options.length; i++) {
                if (selectElement.options[i].value.toLowerCase() === "pending") {
                    selectElement.selectedIndex = i;
                    console.log(`Set default RSVP to "Pending" (index ${i})`);
                    break;
                }
            }
        }
    }

    // Handle delete button clicks
    document.querySelectorAll(".delete-btn").forEach(button => {
        button.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();
            if (confirm("Are you sure you want to delete this guest?")) {
                const guestId = button.getAttribute("data-guest-id");
                const form = document.createElement("form");
                form.method = "POST";
                form.action = "/guest/delete";
                
                const input = document.createElement("input");
                input.type = "hidden";
                input.name = "guestId";
                input.value = guestId;
                
                form.appendChild(input);
                document.body.appendChild(form);
                form.submit();
            }
        });
    });

    // Handle email button clicks
    document.querySelectorAll(".email-btn").forEach(button => {
        button.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();
            const email = button.getAttribute("data-guest-email");
            
            // Show loading state
            const originalIcon = button.innerHTML;
            button.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
            button.disabled = true;
            
            // Send invitation
            fetch('/guest/send-invitation', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `email=${encodeURIComponent(email)}`
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Invitation sent successfully!');
                } else {
                    alert('Failed to send invitation: ' + (data.error || 'Unknown error'));
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Failed to send invitation. Please try again.');
            })
            .finally(() => {
                // Restore button state
                button.innerHTML = originalIcon;
                button.disabled = false;
            });
        });
    });

    // Handle form submission
    guestForm.addEventListener("submit", (e) => {
        e.preventDefault();
        
        // Get all form select elements and ensure they have values
        const selectElements = guestForm.querySelectorAll('select');
        selectElements.forEach(select => {
            if (select.selectedIndex > 0) {
                console.log(`Select ${select.name} value: ${select.value}`);
            } else {
                console.log(`Select ${select.name} has no selection`);
            }
        });
        
        // Make sure RSVP is set
        const rsvpSelect = guestForm.querySelector('#rsvp');
        if (rsvpSelect && rsvpSelect.selectedIndex === 0) {
            console.log("Setting default RSVP to Pending");
            // Find the "Pending" option and select it
            for (let i = 0; i < rsvpSelect.options.length; i++) {
                if (rsvpSelect.options[i].value === "Pending") {
                    rsvpSelect.selectedIndex = i;
                    break;
                }
            }
        }
        
        // Collect form data
        const formData = new FormData(guestForm);
        if (currentGuestId) {
            formData.append("guestId", currentGuestId);
        }
        
        // Log form data for debugging
        console.log("Form data being submitted:");
        for (let [key, value] of formData.entries()) {
            console.log(`${key}: ${value}`);
        }
        
        // Determine if we're in admin project context
        const isAdminProjectPage = window.location.pathname.includes("/project/");
        
        if (currentGuestId) {
            // For edit: check if we're in admin project context or regular
            if (isAdminProjectPage) {
                // Admin edit - use the project controller endpoint
                // Extract project ID from URL path
                const pathParts = window.location.pathname.split('/');
                const projectId = pathParts[2]; // Assuming format /project/{id}/guest
                
                // Create and submit form
                const form = document.createElement("form");
                form.method = "POST";
                form.action = `/project/${projectId}/guest/edit`;
                
                for (let [key, value] of formData.entries()) {
                    const input = document.createElement("input");
                    input.type = "hidden";
                    input.name = key;
                    input.value = value;
                    form.appendChild(input);
                }
                
                document.body.appendChild(form);
                console.log("Submitting admin edit to: " + form.action);
                form.submit();
            } else {
                // Regular user edit - use AJAX to submit to the new endpoint
                console.log("Using AJAX for regular user edit");
                
                // Convert FormData to URL-encoded string
                const params = new URLSearchParams();
                for (let [key, value] of formData.entries()) {
                    params.append(key, value);
                }
                
                // Ensure RSVP is set to a default if it's missing
                if (!formData.has('rsvp') || !formData.get('rsvp')) {
                    console.log("RSVP value missing in form data, setting default to 'Pending'");
                    params.set('rsvp', 'Pending');
                }
                
                // Make sure all required fields are present
                const requiredFields = ['title', 'name', 'email', 'entourage', 'rsvp'];
                let missingFields = [];
                
                requiredFields.forEach(field => {
                    if (!params.has(field) || !params.get(field)) {
                        missingFields.push(field);
                    }
                });
                
                if (missingFields.length > 0) {
                    console.error(`Missing required fields: ${missingFields.join(', ')}`);
                    alert(`Cannot submit form. Missing required fields: ${missingFields.join(', ')}`);
                    return;
                }
                
                // Submit via fetch API
                fetch('/guest/edit-ajax', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: params.toString()
                })
                .then(response => response.json())
                .then(data => {
                    console.log("Response:", data);
                    if (data.success) {
                        alert(data.message);
                        modal.style.display = "none";
                        // Reload page to show updated data
                        window.location.reload();
                    } else {
                        alert("Error: " + data.error);
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                    alert("An error occurred: " + error.message);
                });
            }
        } else {
            // For new guest: use the original form's action
            const form = document.createElement("form");
            form.method = "POST";
            form.action = guestForm.getAttribute("action");
            
            for (let [key, value] of formData.entries()) {
                const input = document.createElement("input");
                input.type = "hidden";
                input.name = key;
                input.value = value;
                form.appendChild(input);
            }
            
            document.body.appendChild(form);
            console.log("Submitting new guest to: " + form.action);
            form.submit();
        }
    });

    // Search functionality
    const searchInput = document.querySelector('.search-box input[type="text"]');
    const guestTable = document.querySelector('table tbody');
    
    if (searchInput && guestTable) {
        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase().trim();
            const rows = guestTable.querySelectorAll('tr');
            
            rows.forEach(row => {
                const cells = row.querySelectorAll('td');
                let found = false;
                
                // Skip the last cell (actions column)
                for (let i = 0; i < cells.length - 1; i++) {
                    const cellText = cells[i].textContent.toLowerCase().trim();
                    if (cellText.includes(searchTerm)) {
                        found = true;
                        break;
                    }
                }
                
                row.style.display = found ? '' : 'none';
            });
        });
    }
});
