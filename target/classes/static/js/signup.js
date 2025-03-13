document.addEventListener("DOMContentLoaded", function () {
    var form = document.getElementById("registrationForm");
    var weddingDateTimeInput = document.getElementById("weddingDateTime");
    var errorMessage = document.getElementById("date-error-message");

    // Set min date-time to current time
    var now = new Date();
    var minDateTime = now.toISOString().slice(0, 16); // Format YYYY-MM-DDTHH:MM
    weddingDateTimeInput.setAttribute("min", minDateTime);

    form.addEventListener("submit", function (event) {
        var selectedDateTime = new Date(weddingDateTimeInput.value);
        var currentDateTime = new Date();

        // Check if selected date-time is in the past
        if (selectedDateTime < currentDateTime) {
            event.preventDefault(); // Prevent form submission
            errorMessage.textContent = "Wedding date & time cannot be in the past!";
            errorMessage.style.color = "red";
        } else {
            errorMessage.textContent = ""; // Clear the error message
        }
    });
});
