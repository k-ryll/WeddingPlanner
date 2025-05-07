$(document).ready(function() {
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
        
        // Send request to server
        $.ajax({
            url: '/project/' + projectId + '/seatplan/add-table',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(tableData),
            success: function(response) {
                // Hide modal and reset form
                $('#addTableModal').hide();
                $('#addTableForm')[0].reset();
                // Reload the page to show the new table
                window.location.reload();
            },
            error: function(xhr) {
                if (xhr.status === 200 || xhr.status === 201) {
                    // If the server responded with success but jQuery marked it as error
                    $('#addTableModal').hide();
                    $('#addTableForm')[0].reset();
                    window.location.reload();
                } else {
                    alert('Unable to add table. Please try again.');
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
            url: '/project/' + projectId + '/seatplan/update-table/' + tableId,
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
            error: function(xhr) {
                if (xhr.status === 200 || xhr.status === 201) {
                    // If the server responded with success but jQuery marked it as error
                    $('#editTableModal').hide();
                    $('#editTableForm')[0].reset();
                    window.location.reload();
                } else {
                    alert('Unable to update table. Please try again.');
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
                url: '/project/' + projectId + '/seatplan/delete-table/' + tableId,
                method: 'DELETE',
                success: function() {
                    $('.table[data-id="' + tableId + '"]').remove();
                },
                error: function(xhr) {
                    console.error('Error deleting table:', xhr);
                    alert('Error deleting table. Please try again.');
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
            url: '/project/' + projectId + '/seatplan/update-positions',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(tables),
            success: function() {
                alert('Layout saved successfully!');
            },
            error: function(xhr) {
                alert('Error saving layout: ' + xhr.responseText);
            }
        });
    });

    // Close modals when clicking outside
    $(window).click(function(e) {
        if ($(e.target).hasClass('modal')) {
            $('#addTableModal').hide();
            $('#editTableModal').hide();
            $('#addTableForm')[0].reset();
            $('#editTableForm')[0].reset();
        }
    });

    // Prevent modal from closing when clicking inside
    $('.modal-content').click(function(e) {
        e.stopPropagation();
    });

    function createTableElement(table) {
        const tableElement = $('<div>')
            .addClass('table')
            .attr('data-table-id', table.id)
            .css({
                left: table.positionX + 'px',
                top: table.positionY + 'px'
            })
            .append($('<div>').addClass('table-name').text(table.tableName))
            .append($('<div>').addClass('chair-count').text(table.numberOfChairs + ' chairs'))
            .append($('<div>').addClass('chairs-container'));

        // Add chair circles
        for (let i = 0; i < table.numberOfChairs; i++) {
            const chair = $('<div>')
                .addClass('chair')
                .attr('data-chair-id', table.chairs[i]?.id)
                .click(function() {
                    openGuestAssignmentModal($(this).attr('data-chair-id'));
                });

            if (table.chairs[i]?.guest) {
                chair.addClass('occupied')
                    .text(getInitials(table.chairs[i].guest.name));
            }

            tableElement.find('.chairs-container').append(chair);
        }

        return tableElement;
    }

    function getInitials(name) {
        return name.split(' ')
            .map(word => word.charAt(0))
            .join('')
            .toUpperCase();
    }
});

function updateTablePosition(tableElement) {
    const position = tableElement.position();
    $.ajax({
        url: '/project/' + projectId + '/seatplan/update-positions',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify([{
            id: tableElement.data('id'),
            positionX: Math.round(position.left),
            positionY: Math.round(position.top)
        }]),
        error: function(xhr) {
            alert('Error updating table position: ' + xhr.responseText);
        }
    });
}

function addTableToPlan(table) {
    const tableElement = $('<div>')
        .addClass('table')
        .data('id', table.id)
        .css({
            left: table.positionX + 'px',
            top: table.positionY + 'px'
        })
        .append($('<div>').addClass('table-header')
            .append($('<div>').addClass('table-name').text(table.tableName))
            .append($('<div>').addClass('table-actions')
                .append($('<button>').addClass('table-action-btn edit-btn').html('<i class="fas fa-edit"></i> Edit').data('id', table.id))
                .append($('<button>').addClass('table-action-btn delete-btn').html('<i class="fas fa-trash"></i> Delete').data('id', table.id))))
        .append($('<div>').addClass('chair-count').text(table.numberOfChairs + ' chairs'))
        .append($('<div>').addClass('chairs-container'));

    // Add chair circles
    for (let i = 0; i < table.numberOfChairs; i++) {
        tableElement.find('.chairs-container').append($('<div>').addClass('chair'));
    }

    tableElement.draggable({
        containment: '#seatPlanArea',
        stop: function(event, ui) {
            updateTablePosition($(this));
        }
    });

    $('#seatPlanArea').append(tableElement);
}

function updateTableInPlan(table) {
    const tableElement = $('.table[data-id="' + table.id + '"]');
    tableElement.find('.table-name').text(table.tableName);
    tableElement.find('.chair-count').text(table.numberOfChairs + ' chairs');
    
    const chairsContainer = tableElement.find('.chairs-container');
    chairsContainer.empty();
    
    for (let i = 0; i < table.numberOfChairs; i++) {
        chairsContainer.append($('<div>').addClass('chair'));
    }
}

function closeModal() {
    $('#addTableModal').hide();
    $('#addTableForm')[0].reset();
}

function closeEditModal() {
    $('#editTableModal').hide();
    $('#editTableForm')[0].reset();
} 