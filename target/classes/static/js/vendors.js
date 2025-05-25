document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('vendorSearch');
    const categoryFilter = document.getElementById('categoryFilter');
    const vendorCards = document.querySelectorAll('.vendor-card');
    
    // Function to filter vendors
    function filterVendors() {
        const searchTerm = searchInput.value.toLowerCase();
        const selectedCategory = categoryFilter.value.toLowerCase();
        
        vendorCards.forEach(card => {
            const vendorName = card.querySelector('.vendor-name').textContent.toLowerCase();
            const vendorCategory = card.querySelector('.vendor-category span').textContent.toLowerCase();
            const vendorLocation = card.getAttribute('data-location').toLowerCase();
            
            // Check if the card matches search and category filter
            const matchesSearch = !searchTerm || vendorName.includes(searchTerm) || 
                                  vendorCategory.includes(searchTerm) || vendorLocation.includes(searchTerm);
            const matchesCategory = !selectedCategory || vendorCategory.includes(selectedCategory);
            
            const isVisible = matchesSearch && matchesCategory;
            card.style.display = isVisible ? 'block' : 'none';
        });
    }
    
    // Attach event listeners
    searchInput.addEventListener('input', filterVendors);
    categoryFilter.addEventListener('change', filterVendors);

    // Only disable elements if project is completed
    const completedMessage = document.querySelector('.completed-message');
    if (completedMessage) {
        const header = document.querySelector('header');
        const allElements = document.querySelectorAll('input, button, select, textarea, a:not([href^="/"]), [onclick], .vendor-actions a, .contact-button');
        
        allElements.forEach(element => {
            // Skip elements in the header
            if (!header.contains(element)) {
                if (element.tagName === 'INPUT' || element.tagName === 'SELECT' || element.tagName === 'TEXTAREA') {
                    element.disabled = true;
                } else if (element.tagName === 'BUTTON' || element.tagName === 'A') {
                    element.style.pointerEvents = 'none';
                    element.style.opacity = '0.6';
                    element.style.cursor = 'not-allowed';
                }
                // Remove onclick handlers
                element.removeAttribute('onclick');
            }
        });

        // Specifically target vendor action buttons and contact links
        document.querySelectorAll('.vendor-actions a, .contact-button').forEach(element => {
            element.style.pointerEvents = 'none';
            element.style.opacity = '0.6';
            element.style.cursor = 'not-allowed';
            element.removeAttribute('onclick');
        });
    }
}); 