document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("registrationForm");
    const password = document.getElementById("password");
    const confirmPass = document.getElementById("confirmPass");
    const passwordErrorMessage = document.getElementById("error-message");
    const weddingDateTimeInput = document.getElementById("weddingDateTime");
    const dateErrorMessage = document.getElementById("date-error-message");

    // Terms and conditions modal functionality
    const termsLink = document.getElementById("termsLink");
    const modal = document.getElementById("termsModal");
    const overlay = document.getElementById("modalOverlay");
    const closeModalButton = document.getElementById("closeModal");

    // Modal event listeners
    if (termsLink && modal && overlay && closeModalButton) {
        termsLink.addEventListener("click", function (event) {
            event.preventDefault();
            modal.style.display = "block";
            overlay.style.display = "block";
        });

        closeModalButton.addEventListener("click", function () {
            modal.style.display = "none";
            overlay.style.display = "none";
        });

        overlay.addEventListener("click", function () {
            modal.style.display = "none";
            overlay.style.display = "none";
        });
    } else {
        console.error("One or more modal elements not found:", {
            termsLink: !!termsLink,
            modal: !!modal,
            overlay: !!overlay,
            closeModalButton: !!closeModalButton
        });
    }

    // Password confirmation validation
    if (form && password && confirmPass && passwordErrorMessage) {
        form.addEventListener("submit", function (event) {
            if (password.value !== confirmPass.value) {
                event.preventDefault();
                passwordErrorMessage.textContent = "Passwords do not match!";
                passwordErrorMessage.style.color = "red";
            } else {
                passwordErrorMessage.textContent = "";
            }
        });
    }

    // Wedding date validation (if applicable)
    if (weddingDateTimeInput && dateErrorMessage) {
        const now = new Date();
        const minDateTime = now.toISOString().slice(0, 16);
        weddingDateTimeInput.setAttribute("min", minDateTime);

        form.addEventListener("submit", function (event) {
            const selectedDateTime = new Date(weddingDateTimeInput.value);
            const currentDateTime = new Date();

            if (selectedDateTime < currentDateTime) {
                event.preventDefault();
                dateErrorMessage.textContent = "Wedding date & time cannot be in the past!";
                dateErrorMessage.style.color = "red";
            } else {
                dateErrorMessage.textContent = "";
            }
        });
    }
});
