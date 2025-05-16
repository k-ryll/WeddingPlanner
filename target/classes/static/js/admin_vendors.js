document.addEventListener('DOMContentLoaded', function() {
    // Initialize price fields 
    togglePriceFields();
    
    // Initialize categories input - add form
    const categoryInput = document.getElementById('categoryInput');
    const categoriesTags = document.getElementById('categoriesTags');
    const categoriesHidden = document.getElementById('categories');
    
    if (categoryInput) {
        categoryInput.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' || e.key === ',') {
                e.preventDefault();
                const value = this.value.trim();
                if (value) {
                    addCategory(value);
                    this.value = '';
                }
            }
        });
    }
    
    // Initialize categories input - edit form
    const editCategoryInput = document.getElementById('editCategoryInput');
    const editCategoriesTags = document.getElementById('editCategoriesTags');
    const editCategoriesHidden = document.getElementById('editCategories');
    
    if (editCategoryInput) {
        editCategoryInput.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' || e.key === ',') {
                e.preventDefault();
                const value = this.value.trim();
                if (value) {
                    addEditCategory(value);
                    this.value = '';
                }
            }
        });
    }
    
    // Update hidden input with categories on form submit
    const addVendorForm = document.querySelector('form[action="/admin/vendors/add"]');
    if (addVendorForm) {
        addVendorForm.addEventListener('submit', function(e) {
            updateCategoriesValue();
            
            // Prevent submission if categories are empty
            const categoriesValue = document.getElementById('categories').value;
            if (!categoriesValue.trim()) {
                e.preventDefault();
                alert("Please add at least one category");
                return false;
            }
        });
    }
    
    const editVendorForm = document.getElementById('editVendorForm');
    if (editVendorForm) {
        editVendorForm.addEventListener('submit', function(e) {
            updateEditCategoriesValue();
            
            // Prevent submission if categories are empty
            const categoriesValue = document.getElementById('editCategories').value;
            if (!categoriesValue.trim()) {
                e.preventDefault();
                alert("Please add at least one category");
                return false;
            }
        });
    }
});

function addCategory(value) {
    const categoriesTags = document.getElementById('categoriesTags');
    
    // Check if the category already exists
    const existingTags = categoriesTags.querySelectorAll('.tag-text');
    for (const tag of existingTags) {
        if (tag.textContent.toLowerCase() === value.toLowerCase()) {
            return; // Skip duplicate categories
        }
    }
    
    const tag = document.createElement('div');
    tag.className = 'tag';
    tag.innerHTML = `
        <span class="tag-text">${value}</span>
        <span class="tag-remove" onclick="removeCategory(this)">×</span>
    `;
    
    categoriesTags.appendChild(tag);
    updateCategoriesValue();
}

function removeCategory(element) {
    const tag = element.closest('.tag');
    tag.remove();
    updateCategoriesValue();
}

function updateCategoriesValue() {
    const categoriesTags = document.getElementById('categoriesTags');
    const categoriesHidden = document.getElementById('categories');
    
    const tags = categoriesTags.querySelectorAll('.tag-text');
    const values = Array.from(tags).map(tag => tag.textContent);
    
    categoriesHidden.value = values.join(', ');
}

function addSuggestedCategory(element) {
    const value = element.getAttribute('data-value');
    addCategory(value);
}

function addEditCategory(value) {
    const categoriesTags = document.getElementById('editCategoriesTags');
    
    // Check if the category already exists
    const existingTags = categoriesTags.querySelectorAll('.tag-text');
    for (const tag of existingTags) {
        if (tag.textContent.toLowerCase() === value.toLowerCase()) {
            return; // Skip duplicate categories
        }
    }
    
    const tag = document.createElement('div');
    tag.className = 'tag';
    tag.innerHTML = `
        <span class="tag-text">${value}</span>
        <span class="tag-remove" onclick="removeEditCategory(this)">×</span>
    `;
    
    categoriesTags.appendChild(tag);
    updateEditCategoriesValue();
}

function removeEditCategory(element) {
    const tag = element.closest('.tag');
    tag.remove();
    updateEditCategoriesValue();
}

function updateEditCategoriesValue() {
    const categoriesTags = document.getElementById('editCategoriesTags');
    const categoriesHidden = document.getElementById('editCategories');
    
    const tags = categoriesTags.querySelectorAll('.tag-text');
    const values = Array.from(tags).map(tag => tag.textContent);
    
    categoriesHidden.value = values.join(', ');
}

function addSuggestedEditCategory(element) {
    const value = element.getAttribute('data-value');
    addEditCategory(value);
}

// Modal functions
function openVendorModal() {
    const modal = document.getElementById('addVendorModal');
    modal.classList.add('show');
    document.body.style.overflow = 'hidden'; // Prevent scrolling behind modal
}

function closeVendorModal() {
    const modal = document.getElementById('addVendorModal');
    modal.classList.remove('show');
    document.body.style.overflow = ''; // Restore scrolling
}

function openEditVendorModal(vendorId) {
    // Find the edit button with the correct vendor ID to get the data attributes
    const editButton = document.querySelector(`.edit-btn[data-id="${vendorId}"]`);
    if (!editButton) return;
    
    // Set up form action
    const form = document.getElementById('editVendorForm');
    form.action = '/admin/vendors/' + vendorId + '/edit';
    
    // Set the vendor ID in the hidden field
    document.getElementById('editVendorId').value = vendorId;
    
    // Populate form fields with vendor data from data attributes
    document.getElementById('editName').value = editButton.getAttribute('data-name');
    document.getElementById('editLocation').value = editButton.getAttribute('data-location') || '';
    
    // Set price type and toggle corresponding fields
    const priceType = editButton.getAttribute('data-price-type');
    document.getElementById('editPriceType').value = priceType;
    toggleEditPriceFields(); // Update price field visibility
    
    // Set price fields based on price type
    if (priceType === 'fixed') {
        document.getElementById('editTotalPrice').value = editButton.getAttribute('data-total-price') || '';
    } else {
        document.getElementById('editPricePerGuest').value = editButton.getAttribute('data-price-per-guest') || '';
    }
    
    // Set contact and description
    document.getElementById('editContact').value = editButton.getAttribute('data-contact') || '';
    document.getElementById('editDescription').value = editButton.getAttribute('data-description') || '';
    
    // Set categories
    const categories = editButton.getAttribute('data-categories');
    document.getElementById('editCategoriesTags').innerHTML = ''; // Clear existing tags
    document.getElementById('editCategories').value = categories;
    
    // Create tag for each category
    if (categories) {
        const categoryArray = categories.split(', ');
        categoryArray.forEach(category => {
            if (category.trim()) {
                addEditCategory(category.trim());
            }
        });
    }
    
    // Show the modal
    const modal = document.getElementById('editVendorModal');
    modal.classList.add('show');
    document.body.style.overflow = 'hidden';
}

function closeEditVendorModal() {
    const modal = document.getElementById('editVendorModal');
    modal.classList.remove('show');
    document.body.style.overflow = '';
}

function confirmDelete(vendorId) {
    if (confirm('Are you sure you want to delete this vendor?')) {
        // Create a form to submit the delete request
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/admin/vendors/' + vendorId + '/delete';
        document.body.appendChild(form);
        form.submit();
    }
}

// Close modal when clicking outside of it
window.onclick = function(event) {
    const addModal = document.getElementById('addVendorModal');
    const editModal = document.getElementById('editVendorModal');
    
    if (event.target === addModal) {
        closeVendorModal();
    } else if (event.target === editModal) {
        closeEditVendorModal();
    }
}

// Toggle price fields based on price type selection
function togglePriceFields() {
    const priceType = document.getElementById('priceType').value;
    const fixedPriceGroup = document.getElementById('fixedPriceGroup');
    const perGuestPriceGroup = document.getElementById('perGuestPriceGroup');
    
    if (priceType === 'fixed') {
        fixedPriceGroup.style.display = 'flex';
        perGuestPriceGroup.style.display = 'none';
        document.getElementById('totalPrice').required = true;
        document.getElementById('pricePerGuest').required = false;
        document.getElementById('pricePerGuest').value = '';
    } else {
        fixedPriceGroup.style.display = 'none';
        perGuestPriceGroup.style.display = 'flex';
        document.getElementById('totalPrice').required = false;
        document.getElementById('pricePerGuest').required = true;
        document.getElementById('totalPrice').value = '';
    }
}

function toggleEditPriceFields() {
    const priceType = document.getElementById('editPriceType').value;
    const fixedPriceGroup = document.getElementById('editFixedPriceGroup');
    const perGuestPriceGroup = document.getElementById('editPerGuestPriceGroup');
    
    if (priceType === 'fixed') {
        fixedPriceGroup.style.display = 'flex';
        perGuestPriceGroup.style.display = 'none';
        document.getElementById('editTotalPrice').required = true;
        document.getElementById('editPricePerGuest').required = false;
        document.getElementById('editPricePerGuest').value = '';
    } else {
        fixedPriceGroup.style.display = 'none';
        perGuestPriceGroup.style.display = 'flex';
        document.getElementById('editTotalPrice').required = false;
        document.getElementById('editPricePerGuest').required = true;
        document.getElementById('editTotalPrice').value = '';
    }
}

function searchVendors() {
    const searchInput = document.getElementById('vendorSearch').value.toLowerCase();
    const vendorCards = document.querySelectorAll('.vendor-card');
    
    vendorCards.forEach(card => {
        const vendorName = card.getAttribute('data-name').toLowerCase();
        const vendorCategory = card.getAttribute('data-category').toLowerCase();
        const vendorLocation = card.getAttribute('data-location').toLowerCase();
        
        if (vendorName.includes(searchInput) || vendorCategory.includes(searchInput) || vendorLocation.includes(searchInput)) {
            card.style.display = '';
        } else {
            card.style.display = 'none';
        }
    });
} 