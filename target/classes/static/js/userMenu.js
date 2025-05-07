document.addEventListener('DOMContentLoaded', function() {
    const userIcons = document.querySelectorAll('.user-icon');
    const dropdowns = document.querySelectorAll('.user-dropdown');

    // Close all dropdowns when clicking outside
    document.addEventListener('click', function(event) {
        const isClickInside = event.target.closest('.user-menu');
        if (!isClickInside) {
            dropdowns.forEach(dropdown => {
                dropdown.classList.remove('show');
            });
        }
    });

    // Toggle dropdown on icon click
    userIcons.forEach(icon => {
        icon.addEventListener('click', function(event) {
            event.stopPropagation();
            const dropdown = this.nextElementSibling;
            
            // Close all other dropdowns
            dropdowns.forEach(d => {
                if (d !== dropdown) {
                    d.classList.remove('show');
                }
            });
            
            // Toggle current dropdown
            dropdown.classList.toggle('show');
        });
    });
}); 