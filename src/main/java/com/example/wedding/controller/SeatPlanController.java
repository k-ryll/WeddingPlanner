package com.example.wedding.controller;

import com.example.wedding.model.*;
import com.example.wedding.service.*;
import com.example.wedding.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SeatPlanController {
    @Autowired
    private SeatPlanService seatPlanService;
    
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ChairService chairService;

    @Autowired
    private GuestService guestService;

    // TableDTO to avoid circular references
    private static class TableDTO {
        private Integer id;
        private String tableName;
        private Integer numberOfChairs;
        private Integer positionX;
        private Integer positionY;
        
        public TableDTO(WeddingTable table) {
            this.id = table.getId();
            this.tableName = table.getTableName();
            this.numberOfChairs = table.getNumberOfChairs();
            this.positionX = table.getPositionX();
            this.positionY = table.getPositionY();
        }
        
        public Integer getId() { return id; }
        public String getTableName() { return tableName; }
        public Integer getNumberOfChairs() { return numberOfChairs; }
        public Integer getPositionX() { return positionX; }
        public Integer getPositionY() { return positionY; }
    }

    // User seat plan route
    @GetMapping("/seatplan")
    public String getUserSeatPlan(@SessionAttribute(name = "loggedUser", required = false) User user, Model model) {
        if (user == null) {
            return "redirect:/user/login";
        }
        
        Project project = projectService.findProjectByUserEmail(user.getEmail());
        
        if (project == null) {
            model.addAttribute("project", null);
            model.addAttribute("seatPlan", null);
            model.addAttribute("guests", Collections.emptyList());
            model.addAttribute("error", "No project found. Please create a project to use the seat planning tool.");
            return "user-seat-plan";
        }
        
        SeatPlan seatPlanInstance = seatPlanService.getSeatPlanByProjectId(project.getId());
        if (seatPlanInstance == null) {
            seatPlanInstance = new SeatPlan();
            seatPlanInstance.setProject(project);
            seatPlanInstance.setTables(new java.util.ArrayList<>()); 
            seatPlanInstance = seatPlanService.saveSeatPlan(seatPlanInstance);
            model.addAttribute("info", "A new seat plan has been initialized for your project.");
        }
        
        final SeatPlan finalSeatPlan = seatPlanInstance;
        List<Guest> allGuests = guestService.getGuestsByProject(project);
        List<Guest> unassignedGuests = allGuests.stream()
            .filter(g -> finalSeatPlan.getTables().stream()
                .flatMap(table -> table.getChairs().stream())
                .noneMatch(chair -> g.equals(chair.getGuest())))
            .collect(Collectors.toList());
        
        model.addAttribute("project", project);
        model.addAttribute("seatPlan", finalSeatPlan);
        model.addAttribute("guests", unassignedGuests);
        return "user-seat-plan";
    }

    // Admin seat plan route
    @GetMapping("/project/{projectId}/seatplan")
    public String getAdminSeatPlan(@PathVariable Integer projectId, Model model) {
        Project project = projectService.findById(projectId);
        if (project == null) {
            return "redirect:/admin/home?error=ProjectNotFound"; 
        }
        
        SeatPlan seatPlanInstance = seatPlanService.getSeatPlanByProjectId(project.getId());
        if (seatPlanInstance == null) {
            seatPlanInstance = new SeatPlan();
            seatPlanInstance.setProject(project);
            seatPlanInstance.setTables(new java.util.ArrayList<>());
            seatPlanInstance = seatPlanService.saveSeatPlan(seatPlanInstance); 
            model.addAttribute("info", "A new seat plan has been initialized for this project.");
        }
        
        final SeatPlan finalSeatPlan = seatPlanInstance;
        List<Guest> allGuests = guestService.getGuestsByProject(project);
        List<Guest> unassignedGuests = allGuests.stream()
            .filter(g -> finalSeatPlan.getTables().stream()
                .flatMap(table -> table.getChairs().stream())
                .noneMatch(chair -> g.equals(chair.getGuest())))
            .collect(Collectors.toList());
        
        model.addAttribute("project", project);
        model.addAttribute("seatPlan", finalSeatPlan);
        model.addAttribute("guests", unassignedGuests);
        return "seat-plan";
    }

    // User endpoints
    @PostMapping("/seatplan/assign-guest")
    @ResponseBody
    public ResponseEntity<ChairDTO> assignGuestToChair(
            @SessionAttribute(name = "loggedUser", required = false) User user,
            @RequestParam Integer chairId,
            @RequestParam Integer guestId) {
        try {
            if (user == null) {
                return ResponseEntity.status(401).build();
            }

            Project project = projectService.findProjectByUserEmail(user.getEmail());
            if (project == null) {
                return ResponseEntity.status(404).build();
            }

            Guest guest = guestService.findById(guestId);
            if (guest == null) {
                return ResponseEntity.notFound().build();
            }
            
            Chair chair = chairService.assignGuestToChair(chairId, guest);
            if (chair == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Convert to DTO to avoid circular references
            GuestDTO guestDTO = new GuestDTO(guest.getGuestId(), guest.getName(), guest.getEmail());
            ChairDTO chairDTO = new ChairDTO(chair.getId(), chair.getPosition(), guestDTO);
            
            return ResponseEntity.ok(chairDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/seatplan/remove-guest")
    @ResponseBody
    public ResponseEntity<ChairDTO> removeGuestFromChair(
            @SessionAttribute(name = "loggedUser", required = false) User user,
            @RequestParam Integer chairId) {
        try {
            if (user == null) {
                return ResponseEntity.status(401).build();
            }

            Project project = projectService.findProjectByUserEmail(user.getEmail());
            if (project == null) {
                return ResponseEntity.status(404).build();
            }

            chairService.removeGuestFromChair(chairId);
            Chair chair = chairService.getChairById(chairId);
            
            // Convert to DTO to avoid circular references
            ChairDTO chairDTO = new ChairDTO(chair.getId(), chair.getPosition(), null);
            
            return ResponseEntity.ok(chairDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/seatplan/add-table")
    @ResponseBody
    public ResponseEntity<TableDTO> addTable(
            @SessionAttribute(name = "loggedUser", required = false) User user,
            @RequestBody WeddingTable table) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Project project = projectService.findProjectByUserEmail(user.getEmail());
        if (project == null) {
            return ResponseEntity.status(404).build();
        }

        SeatPlan seatPlan = seatPlanService.getSeatPlanByProjectId(project.getId());
        if (seatPlan == null) {
            return ResponseEntity.notFound().build();
        }

        // Set up the new table
        table.setSeatPlan(seatPlan);
        table.initializeChairs();
        
        // Add table to seat plan
        seatPlan.getTables().add(table);
        
        // Save and return the updated seat plan
        seatPlanService.saveSeatPlan(seatPlan);
        
        // Return DTO instead of full entity to avoid circular references
        return ResponseEntity.ok(new TableDTO(table));
    }

    @PostMapping("/seatplan/update-positions")
    @ResponseBody
    public ResponseEntity<String> updateTablePositions(
            @SessionAttribute(name = "loggedUser", required = false) User user,
            @RequestBody List<WeddingTable> tables) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Project project = projectService.findProjectByUserEmail(user.getEmail());
        if (project == null) {
            return ResponseEntity.status(404).build();
        }

        SeatPlan seatPlan = seatPlanService.getSeatPlanByProjectId(project.getId());
        
        // Update each table's position
        for (WeddingTable updatedTable : tables) {
            for (WeddingTable existingTable : seatPlan.getTables()) {
                if (existingTable.getId().equals(updatedTable.getId())) {
                    existingTable.setPositionX(updatedTable.getPositionX());
                    existingTable.setPositionY(updatedTable.getPositionY());
                    break;
                }
            }
        }
        
        seatPlanService.saveSeatPlan(seatPlan);
        return ResponseEntity.ok("Table positions updated successfully");
    }

    @PutMapping("/seatplan/update-table/{tableId}")
    @ResponseBody
    public ResponseEntity<WeddingTable> updateTable(
            @SessionAttribute(name = "loggedUser", required = false) User user,
            @PathVariable Integer tableId,
            @RequestBody WeddingTable updatedTable) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Project project = projectService.findProjectByUserEmail(user.getEmail());
        if (project == null) {
            return ResponseEntity.status(404).build();
        }

        SeatPlan seatPlan = seatPlanService.getSeatPlanByProjectId(project.getId());
        if (seatPlan == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Find and update the table
        for (WeddingTable table : seatPlan.getTables()) {
            if (table.getId().equals(tableId)) {
                table.setTableName(updatedTable.getTableName());
                table.setNumberOfChairs(updatedTable.getNumberOfChairs());
                table.initializeChairs();
                seatPlanService.saveSeatPlan(seatPlan);
                return ResponseEntity.ok(table);
            }
        }
        
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/seatplan/delete-table/{tableId}")
    @ResponseBody
    public ResponseEntity<Void> deleteTable(
            @SessionAttribute(name = "loggedUser", required = false) User user,
            @PathVariable Integer tableId) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Project project = projectService.findProjectByUserEmail(user.getEmail());
        if (project == null) {
            return ResponseEntity.status(404).build();
        }

        SeatPlan seatPlan = seatPlanService.getSeatPlanByProjectId(project.getId());
        
        // Remove the table
        seatPlan.getTables().removeIf(table -> table.getId().equals(tableId));
        seatPlanService.saveSeatPlan(seatPlan);
        
        return ResponseEntity.ok().build();
    }

    // Admin endpoints
    @PostMapping("/project/{projectId}/seatplan/assign-guest")
    @ResponseBody
    public ResponseEntity<ChairDTO> assignGuestToChairAdmin(
            @PathVariable Integer projectId,
            @RequestParam Integer chairId,
            @RequestParam Integer guestId) {
        try {
            Guest guest = guestService.findById(guestId);
            if (guest == null) {
                return ResponseEntity.notFound().build();
            }
            
            Chair chair = chairService.assignGuestToChair(chairId, guest);
            if (chair == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Convert to DTO to avoid circular references
            GuestDTO guestDTO = new GuestDTO(guest.getGuestId(), guest.getName(), guest.getEmail());
            ChairDTO chairDTO = new ChairDTO(chair.getId(), chair.getPosition(), guestDTO);
            
            return ResponseEntity.ok(chairDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/project/{projectId}/seatplan/remove-guest")
    @ResponseBody
    public ResponseEntity<ChairDTO> removeGuestFromChairAdmin(
            @PathVariable Integer projectId,
            @RequestParam Integer chairId) {
        try {
            chairService.removeGuestFromChair(chairId);
            Chair chair = chairService.getChairById(chairId);
            
            // Convert to DTO to avoid circular references
            ChairDTO chairDTO = new ChairDTO(chair.getId(), chair.getPosition(), null);
            
            return ResponseEntity.ok(chairDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/project/{projectId}/seatplan/add-table")
    @ResponseBody
    public ResponseEntity<TableDTO> addTableAdmin(
            @PathVariable Integer projectId,
            @RequestBody WeddingTable table) {
        SeatPlan seatPlan = seatPlanService.getSeatPlanByProjectId(projectId);
        if (seatPlan == null) {
            return ResponseEntity.notFound().build();
        }

        // Set up the new table
        table.setSeatPlan(seatPlan);
        table.initializeChairs();
        
        // Add table to seat plan
        seatPlan.getTables().add(table);
        
        // Save and return the updated seat plan
        seatPlanService.saveSeatPlan(seatPlan);
        
        // Return DTO instead of full entity to avoid circular references
        return ResponseEntity.ok(new TableDTO(table));
    }

    @PostMapping("/project/{projectId}/seatplan/update-positions")
    @ResponseBody
    public ResponseEntity<String> updateTablePositionsAdmin(
            @PathVariable Integer projectId,
            @RequestBody List<WeddingTable> tables) {
        SeatPlan seatPlan = seatPlanService.getSeatPlanByProjectId(projectId);
        
        // Update each table's position
        for (WeddingTable updatedTable : tables) {
            for (WeddingTable existingTable : seatPlan.getTables()) {
                if (existingTable.getId().equals(updatedTable.getId())) {
                    existingTable.setPositionX(updatedTable.getPositionX());
                    existingTable.setPositionY(updatedTable.getPositionY());
                    break;
                }
            }
        }
        
        seatPlanService.saveSeatPlan(seatPlan);
        return ResponseEntity.ok("Table positions updated successfully");
    }

    @PutMapping("/project/{projectId}/seatplan/update-table/{tableId}")
    @ResponseBody
    public ResponseEntity<WeddingTable> updateTableAdmin(
            @PathVariable Integer projectId,
            @PathVariable Integer tableId,
            @RequestBody WeddingTable updatedTable) {
        SeatPlan seatPlan = seatPlanService.getSeatPlanByProjectId(projectId);
        if (seatPlan == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Find and update the table
        for (WeddingTable table : seatPlan.getTables()) {
            if (table.getId().equals(tableId)) {
                table.setTableName(updatedTable.getTableName());
                table.setNumberOfChairs(updatedTable.getNumberOfChairs());
                table.initializeChairs();
                seatPlanService.saveSeatPlan(seatPlan);
                return ResponseEntity.ok(table);
            }
        }
        
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/project/{projectId}/seatplan/delete-table/{tableId}")
    @ResponseBody
    public ResponseEntity<Void> deleteTableAdmin(
            @PathVariable Integer projectId,
            @PathVariable Integer tableId) {
        SeatPlan seatPlan = seatPlanService.getSeatPlanByProjectId(projectId);
        
        // Remove the table
        seatPlan.getTables().removeIf(table -> table.getId().equals(tableId));
        seatPlanService.saveSeatPlan(seatPlan);
        
        return ResponseEntity.ok().build();
    }
} 