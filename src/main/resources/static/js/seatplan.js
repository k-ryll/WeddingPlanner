document.addEventListener('DOMContentLoaded', function() {
    const canvas = document.getElementById('seatPlanCanvas');
    const addTableForm = document.getElementById('addTableForm');
    const saveButton = document.getElementById('saveSeatPlan');
    let tables = [];
    let isDragging = false;
    let currentTable = null;
    let offsetX = 0;
    let offsetY = 0;

    // Get project ID from URL
    const urlParts = window.location.pathname.split('/');
    const projectId = urlParts[2]; // URL pattern: /project/{id}/seatplan

    // Load existing tables
    loadTables();

    // Handle form submission
    addTableForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const tableName = document.getElementById('tableName').value;
        const capacity = parseInt(document.getElementById('capacity').value);

        const tableData = {
            tableName: tableName,
            capacity: capacity,
            projectId: projectId,
            x: 100, // Default position
            y: 100  // Default position
        };

        fetch('/api/tables', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(tableData)
        })
        .then(response => {
            if (response.redirected) {
                window.location.href = response.url;
                return;
            }
            return response.json();
        })
        .then(data => {
            if (data) {
                createTableElement(data);
                addTableForm.reset();
            }
        })
        .catch(error => console.error('Error:', error));
    });

    // Handle save button click
    saveButton.addEventListener('click', function() {
        const tableElements = document.querySelectorAll('.table');
        const tablesToSave = Array.from(tableElements).map(tableElement => {
            // Get the computed style for left and top
            const style = window.getComputedStyle(tableElement);
            const x = parseInt(style.left);
            const y = parseInt(style.top);
            
            return {
                id: tableElement.dataset.id,
                tableName: tableElement.querySelector('.table-name').textContent,
                capacity: parseInt(tableElement.querySelectorAll('.seat').length),
                projectId: projectId,
                x: x,
                y: y
            };
        });

        fetch('/api/tables/save-all', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(tablesToSave)
        })
        .then(response => {
            if (response.redirected) {
                window.location.href = response.url;
                return;
            }
            return response.json();
        })
        .then(data => {
            if (data) {
                alert('Seat plan saved successfully!');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Failed to save seat plan. Please try again.');
        });
    });

    function loadTables() {
        // Clear existing tables from the canvas
        while (canvas.firstChild) {
            canvas.removeChild(canvas.firstChild);
        }

        fetch(`/api/tables/project/${projectId}`)
            .then(response => {
                if (response.redirected) {
                    window.location.href = response.url;
                    return;
                }
                return response.json();
            })
            .then(data => {
                if (data) {
                    tables = data;
                    data.forEach(table => createTableElement(table));
                }
            })
            .catch(error => console.error('Error:', error));
    }

    function createTableElement(tableData) {
        const tableElement = document.createElement('div');
        tableElement.className = 'table';
        tableElement.dataset.id = tableData.id;
        tableElement.style.left = `${tableData.x}px`;
        tableElement.style.top = `${tableData.y}px`;

        const tableName = document.createElement('div');
        tableName.className = 'table-name';
        tableName.textContent = tableData.tableName;

        const seatsContainer = document.createElement('div');
        seatsContainer.className = 'seats-container';

        // Create seat circles with numbers
        for (let i = 0; i < tableData.capacity; i++) {
            const seat = document.createElement('div');
            seat.className = 'seat';
            seat.dataset.seatNumber = i + 1;
            seat.textContent = i + 1; // Add seat number
            seatsContainer.appendChild(seat);
        }

        tableElement.appendChild(tableName);
        tableElement.appendChild(seatsContainer);

        // Add drag functionality with passive event listeners
        tableElement.addEventListener('mousedown', startDragging, { passive: false });
        tableElement.addEventListener('touchstart', startDragging, { passive: false });

        canvas.appendChild(tableElement);
    }

    function startDragging(e) {
        e.preventDefault();
        isDragging = true;
        currentTable = this;
        currentTable.classList.add('dragging');
        
        const rect = this.getBoundingClientRect();
        if (e.type === 'mousedown') {
            offsetX = e.clientX - rect.left;
            offsetY = e.clientY - rect.top;
        } else {
            offsetX = e.touches[0].clientX - rect.left;
            offsetY = e.touches[0].clientY - rect.top;
        }

        document.addEventListener('mousemove', drag, { passive: false });
        document.addEventListener('touchmove', drag, { passive: false });
        document.addEventListener('mouseup', stopDragging, { passive: true });
        document.addEventListener('touchend', stopDragging, { passive: true });
    }

    function drag(e) {
        if (!isDragging) return;
        e.preventDefault();

        const x = e.type === 'mousemove' ? e.clientX : e.touches[0].clientX;
        const y = e.type === 'mousemove' ? e.clientY : e.touches[0].clientY;

        const canvasRect = canvas.getBoundingClientRect();
        const maxX = canvasRect.width - currentTable.offsetWidth;
        const maxY = canvasRect.height - currentTable.offsetHeight;

        let newX = x - canvasRect.left - offsetX;
        let newY = y - canvasRect.top - offsetY;

        // Keep table within canvas bounds
        newX = Math.max(0, Math.min(newX, maxX));
        newY = Math.max(0, Math.min(newY, maxY));

        currentTable.style.left = `${newX}px`;
        currentTable.style.top = `${newY}px`;
    }

    function stopDragging() {
        if (currentTable) {
            currentTable.classList.remove('dragging');
        }
        isDragging = false;
        currentTable = null;
        document.removeEventListener('mousemove', drag);
        document.removeEventListener('touchmove', drag);
        document.removeEventListener('mouseup', stopDragging);
        document.removeEventListener('touchend', stopDragging);
    }
}); 