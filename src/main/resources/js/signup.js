document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("registrationForm").addEventListener("submit", function (event) {
        var password = document.getElementById("password").value;
        var confirmPass = document.getElementById("confirmPass").value;
        var errorMessage = document.getElementById("error-message");

        if (password !== confirmPass) {
            event.preventDefault(); // Stop form submission
            errorMessage.textContent = "Passwords do not match!";
            errorMessage.style.color = "red";
        } else {
            errorMessage.textContent = ""; // Clear error message
        }
    });
});
