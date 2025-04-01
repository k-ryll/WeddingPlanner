document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("guestModal");
const openModalBtn = document.getElementById("openModal");
const closeModalBtn = document.querySelector(".close");

openModalBtn.addEventListener("click", () => {
  modal.style.display = "block";
});

closeModalBtn.addEventListener("click", () => {
  modal.style.display = "none";
});

window.addEventListener("click", (event) => {
  if (event.target === modal) {
    modal.style.display = "none";
  }
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
});
