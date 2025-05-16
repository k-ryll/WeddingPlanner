// Global variables for chair selection
let selectedChairId = null;
let selectedChairElement = null;
let projectId, seatPlanId;

// Add window.onload for debugging
window.onload = function() {
    console.log("Window loaded");
    // Get values from data attributes on body
    try {
        projectId = document.body.getAttribute('data-project-id');
        seatPlanId = document.body.getAttribute('data-seat-plan-id');
        console.log("From data attributes - Project ID:", projectId, "SeatPlan ID:", seatPlanId);
    } catch (error) {
        console.error("Error getting data attributes:", error);
    }
    
    // Add direct event listeners to chairs
    console.log("Adding click listeners to chairs");
    document.querySelectorAll('.chair').forEach(function(chair) {
        chair.addEventListener('click', function() {
            console.log("Chair clicked directly");
            openGuestAssignmentModal(this);
        });
    });
};

$(document).ready(function() {
    console.log("Document ready");
    
    // Try to get values from the script tag if not already set
    if (!projectId || !seatPlanId) {
        try {
            // These should be set in the inline script in the HTML
            console.log("From document ready - Project ID:", projectId, "SeatPlan ID:", seatPlanId);
        } catch (error) {
            console.error("Error getting project/seatplan IDs:", error);
        }
    }
    
    // Make tables draggable with improved handling
    $('.table').draggable({
        containment: '#seatPlanArea',
        start: function(e, ui) {
            // Prevent dragging when clicking buttons
            if ($(e.target).hasClass('table-action-btn')) {
                return false;
            }
        },
        stop: function(event, ui) {
            updateTablePosition($(this));
        }
    });

    // Add table button click handler
    $('#addTableBtn').click(function() {
        $('#addTableModal').show();
    });

    // Add table form submission
    $('#addTableForm').submit(function(e) {
        e.preventDefault();
        
        // Get form values
        const tableName = $('#tableName').val().trim();
        const numberOfChairs = parseInt($('#numberOfChairs').val());
        
        // Basic validation
        if (!tableName) {
            alert('Please enter a table name');
            return;
        }
        if (!numberOfChairs || numberOfChairs < 1) {
            alert('Please enter a valid number of chairs');
            return;
        }
        
        // Create table data
        const tableData = {
            tableName: tableName,
            numberOfChairs: numberOfChairs,
            positionX: 100,
            positionY: 100
        };
        
        // Send request to server - using USER endpoint
        $.ajax({
            url: '/seatplan/add-table',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(tableData),
            success: function(response) {
                // Hide modal and reset form
                $('#addTableModal').hide();
                $('#addTableForm')[0].reset();
                // Reload the page to show the new table
                alert('Table added successfully! Refreshing page to display it.');
                window.location.reload();
            },
            error: function(xhr, status, error) {
                console.error('Error adding table:', error);
                console.error('Status:', status);
                console.error('Response:', xhr.responseText);
                if (xhr.status === 200 || xhr.status === 201) {
                    // If the server responded with success but jQuery marked it as error
                    $('#addTableModal').hide();
                    $('#addTableForm')[0].reset();
                    window.location.reload();
                } else {
                    alert('Unable to add table. Please check the console for details.');
                }
            }
        });
    });

    // Edit table button click handler
    $(document).on('click', '.edit-btn', function(e) {
        e.stopPropagation(); // Prevent table dragging when clicking edit
        const tableId = $(this).data('id');
        const table = $(this).closest('.table');
        const tableName = table.find('.table-name').text();
        const chairCount = table.find('.chair').length;
        
        $('#editTableId').val(tableId);
        $('#editTableName').val(tableName);
        $('#editNumberOfChairs').val(chairCount);
        $('#editTableModal').show();
    });

    // Edit table form submission
    $('#editTableForm').submit(function(e) {
        e.preventDefault();
        const tableId = $('#editTableId').val();
        const tableName = $('#editTableName').val().trim();
        const numberOfChairs = parseInt($('#editNumberOfChairs').val());
        
        // Basic validation
        if (!tableName) {
            alert('Please enter a table name');
            return;
        }
        if (!numberOfChairs || numberOfChairs < 1) {
            alert('Please enter a valid number of chairs');
            return;
        }
        
        $.ajax({
            url: `/seatplan/update-table/${tableId}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({
                tableName: tableName,
                numberOfChairs: numberOfChairs
            }),
            success: function(response) {
                // Hide modal and reset form
                $('#editTableModal').hide();
                $('#editTableForm')[0].reset();
                // Reload the page to show the updated table
                window.location.reload();
            },
            error: function(xhr, status, error) {
                console.error('Error updating table:', error);
                console.error('Status:', status);
                console.error('Response:', xhr.responseText);
                if (xhr.status === 200 || xhr.status === 201) {
                    // If the server responded with success but jQuery marked it as error
                    $('#editTableModal').hide();
                    $('#editTableForm')[0].reset();
                    window.location.reload();
                } else {
                    alert('Unable to update table. Please check the console for details.');
                }
            }
        });
    });

    // Delete table button click handler
    $(document).on('click', '.delete-btn', function(e) {
        e.stopPropagation(); // Prevent table dragging when clicking delete
        if (confirm('Are you sure you want to delete this table?')) {
            const tableId = $(this).data('id');
            $.ajax({
                url: `/seatplan/delete-table/${tableId}`,
                method: 'DELETE',
                success: function() {
                    $('.table[data-id="' + tableId + '"]').remove();
                    window.location.reload();
                },
                error: function(xhr, status, error) {
                    console.error('Error deleting table:', xhr);
                    console.error('Status:', status);
                    console.error('Response:', xhr.responseText);
                    alert('Error deleting table. Please check the console for details.');
                }
            });
        }
    });

    // Save button click handler
    $('#saveBtn').click(function() {
        const tables = [];
        $('.table').each(function() {
            const position = $(this).position();
            tables.push({
                id: $(this).data('id'),
                positionX: Math.round(position.left),
                positionY: Math.round(position.top)
            });
        });

        $.ajax({
            url: '/seatplan/update-positions',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(tables),
            success: function() {
                alert('Layout saved successfully!');
            },
            error: function(xhr, status, error) {
                console.error('Error saving layout:', error);
                console.error('Status:', status);
                console.error('Response:', xhr.responseText);
                alert('Error saving layout: ' + (xhr.responseText || ''));
            }
        });
    });

    // Close modals when clicking outside
    $(window).click(function(e) {
        if ($(e.target).hasClass('modal')) {
            $('.modal').hide();
            $('#addTableForm')[0].reset();
            $('#editTableForm')[0].reset();
        }
    });

    // Prevent modal from closing when clicking inside
    $('.modal-content').click(function(e) {
        e.stopPropagation();
    });
});

function updateTablePosition(tableElement) {
    const position = tableElement.position();
    $.ajax({
        url: '/seatplan/update-positions',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify([{
            id: tableElement.data('id'),
            positionX: Math.round(position.left),
            positionY: Math.round(position.top)
        }]),
        error: function(xhr, status, error) {
            console.error('Error updating position:', error);
            console.error('Status:', status);
            console.error('Response:', xhr.responseText);
            alert('Error updating table position. Please check the console for details.');
        }
    });
}

function closeModal() {
    $('#addTableModal').hide();
    $('#addTableForm')[0].reset();
}

function closeEditModal() {
    $('#editTableModal').hide();
    $('#editTableForm')[0].reset();
}

function closeGuestModal() {
    $('#guestAssignmentModal').hide();
    selectedChairId = null;
    selectedChairElement = null;
    $('#guestSelect').val('');
    $('#currentGuestInfo').hide();
}

function assignGuest() {
    console.log("Assigning guest to chair:", selectedChairId);
    if (!selectedChairId) {
        console.error("No chair selected");
        return;
    }
    const guestId = $('#guestSelect').val();
    console.log("Selected guest ID:", guestId);
    if (!guestId) {
        alert('Please select a guest');
        return;
    }

    $.ajax({
        url: '/seatplan/assign-guest',
        method: 'POST',
        data: {
            chairId: selectedChairId,
            guestId: guestId
        },
        success: function(response) {
            console.log("Assign guest response:", response);
            if (response && response.id) {
                if (response.guest) {
                    selectedChairElement.addClass('occupied');
                    selectedChairElement.text(getInitials(response.guest.name));
                    selectedChairElement.data('guest-name', response.guest.name);
                    alert("Guest assigned successfully!");
                } else {
                    selectedChairElement.removeClass('occupied');
                    selectedChairElement.text('');
                    selectedChairElement.data('guest-name', null);
                }
                closeGuestModal();
            } else {
                alert("Failed to assign guest. Please try again.");
            }
        },
        error: function(xhr, status, error) {
            console.error('Error assigning guest:', error);
            console.error('Status:', status);
            console.error('Response:', xhr.responseText);
            alert('Error assigning guest. Please check the console for details.');
        }
    });
}

function removeGuest() {
    console.log("Removing guest from chair:", selectedChairId);
    if (!selectedChairId) {
        console.error("No chair selected");
        return;
    }

    $.ajax({
        url: '/seatplan/remove-guest',
        method: 'POST',
        data: {
            chairId: selectedChairId
        },
        success: function(response) {
            console.log("Remove guest response:", response);
            if (response && response.id) {
                selectedChairElement.removeClass('occupied');
                selectedChairElement.text('');
                selectedChairElement.data('guest-name', null);
                alert("Guest removed successfully!");
                closeGuestModal();
            } else {
                alert("Failed to remove guest. Please try again.");
            }
        },
        error: function(xhr, status, error) {
            console.error('Error removing guest:', error);
            console.error('Status:', status);
            console.error('Response:', xhr.responseText);
            alert('Error removing guest. Please check the console for details.');
        }
    });
}

function getInitials(name) {
    if (!name) return '';
    const parts = name.split(' ');
    if (parts.length > 1) {
        return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
    return name.charAt(0).toUpperCase();
}

// Function to open guest assignment modal
function openGuestAssignmentModal(chairElement) {
    console.log("Opening guest assignment modal called");
    
    if (!projectId || !seatPlanId) {
        console.error("Missing project or seat plan ID", {projectId, seatPlanId});
        alert("Error: Project or Seat Plan not available. Check console for details.");
        return;
    }
    
    selectedChairElement = $(chairElement);
    selectedChairId = selectedChairElement.data('chair-id');
    console.log("Selected chair ID:", selectedChairId);
    
    const currentGuestName = selectedChairElement.data('guest-name');
    const currentGuestInfo = $('#currentGuestInfo');
    const currentGuestNameSpan = $('#currentGuestName');
    
    if (currentGuestName) {
        currentGuestInfo.show();
        currentGuestNameSpan.text(currentGuestName);
    } else {
        currentGuestInfo.hide();
    }
    
    const modal = document.getElementById('guestAssignmentModal');
    if (!modal) {
        console.error("Guest assignment modal not found in DOM");
        alert("Error: Guest assignment modal not found");
        return;
    }
    
    console.log("Displaying modal");
    modal.style.display = 'block';
} 