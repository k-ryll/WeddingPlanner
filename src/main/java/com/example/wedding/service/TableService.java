package com.example.wedding.service;

import com.example.wedding.model.DinnerTable;
import com.example.wedding.model.Project;
import com.example.wedding.model.Seat;
import com.example.wedding.repository.TableRepository;
import com.example.wedding.repository.ProjectRepository;
import com.example.wedding.dto.TableDTO;
import com.example.wedding.dto.SeatDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class TableService {

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    @Transactional
    public TableDTO createTable(TableDTO tableDTO) {
        Project project = projectService.findById(tableDTO.getProjectId());
        if (project == null) {
            throw new IllegalArgumentException("Project not found");
        }

        DinnerTable table = new DinnerTable();
        table.setTableName(tableDTO.getTableName());
        table.setCapacity(tableDTO.getCapacity());
        table.setProject(project);
        table.setX(tableDTO.getX());
        table.setY(tableDTO.getY());

        DinnerTable savedTable = tableRepository.save(table);
        return convertToDTO(savedTable);
    }

    public List<TableDTO> saveAllTables(List<TableDTO> tables) {
        List<DinnerTable> savedTables = new ArrayList<>();
        
        for (TableDTO tableDTO : tables) {
            Project project = projectService.findById(tableDTO.getProjectId());
            if (project == null) {
                throw new IllegalArgumentException("Project not found");
            }

            DinnerTable table;
            if (tableDTO.getId() != null) {
                // Update existing table
                table = tableRepository.findById(tableDTO.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Table not found"));
            } else {
                // Create new table
                table = new DinnerTable();
            }

            table.setTableName(tableDTO.getTableName());
            table.setCapacity(tableDTO.getCapacity());
            table.setProject(project);
            table.setX(tableDTO.getX());
            table.setY(tableDTO.getY());

            savedTables.add(table);
        }
        
        List<DinnerTable> result = tableRepository.saveAll(savedTables);
        return result.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<TableDTO> getTablesByProject(Integer projectId) {
        List<DinnerTable> tables = tableRepository.findByProject_Id(projectId);
        return tables.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void deleteTable(Integer tableId) {
        tableRepository.deleteById(tableId);
    }

    private TableDTO convertToDTO(DinnerTable table) {
        TableDTO dto = new TableDTO();
        dto.setId(table.getId());
        dto.setTableName(table.getTableName());
        dto.setCapacity(table.getCapacity());
        dto.setProjectId(table.getProject().getId());
        dto.setX(table.getX());
        dto.setY(table.getY());

        if (table.getSeats() != null) {
            List<SeatDTO> seatDTOs = table.getSeats().stream()
                    .map(this::convertSeatToDTO)
                    .collect(Collectors.toList());
            dto.setSeats(seatDTOs);
        }

        return dto;
    }

    private SeatDTO convertSeatToDTO(Seat seat) {
        SeatDTO dto = new SeatDTO();
        dto.setSeatId(seat.getSeatId());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setTableId(seat.getTable().getId());
        
        if (seat.getGuest() != null) {
            dto.setGuestId(seat.getGuest().getGuestId());
            dto.setGuestName(seat.getGuest().getName());
        }

        return dto;
    }
} 