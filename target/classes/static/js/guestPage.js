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
            
            // Get guest details from the row
            const title = row.cells[0].textContent;
            const name = row.cells[1].textContent;
            const email = row.cells[2].textContent;
            const entourage = row.cells[3].textContent;
            const rsvp = row.cells[4].textContent;
            
            // Set form values
            document.querySelector(".modal-title").textContent = "Edit Guest";
            guestForm.querySelector("[name='title']").value = title;
            guestForm.querySelector("[name='name']").value = name;
            guestForm.querySelector("[name='email']").value = email;
            guestForm.querySelector("[name='entourage']").value = entourage;
            guestForm.querySelector("[name='rsvp']").value = rsvp;
            
            currentGuestId = guestId;
            modal.style.display = "block";
        });
    });

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

    // Handle form submission
    guestForm.addEventListener("submit", (e) => {
        e.preventDefault();
        
        const formData = new FormData(guestForm);
        if (currentGuestId) {
            formData.append("guestId", currentGuestId);
        }
        
        const form = document.createElement("form");
        form.method = "POST";
        form.action = currentGuestId ? "/guest/edit" : "/guest/create";
        
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

    // Email button click handler
    document.querySelectorAll('.email-btn').forEach(button => {
        button.addEventListener('click', async function() {
            const guestEmail = this.getAttribute('data-guest-email');
            
            try {
                const response = await fetch('/guest/send-invitation', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `email=${encodeURIComponent(guestEmail)}`
                });

                const result = await response.text();
                
                if (response.ok) {
                    alert('Invitation sent successfully!');
                } else {
                    alert('Failed to send invitation: ' + result);
                }
            } catch (error) {
                alert('Error sending invitation: ' + error.message);
            }
        });
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
