package com.example.wedding.controller;

import com.example.wedding.model.*;
import com.example.wedding.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/project/{projectId}/seatplan")
public class SeatPlanController {
    @Autowired
    private SeatPlanService seatPlanService;
    
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ChairService chairService;

    @Autowired
    private GuestService guestService;

    @GetMapping
    public String getSeatPlan(@PathVariable Integer projectId, Model model) {
        Project project = projectService.findById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Project not found");
        }
        
        final SeatPlan seatPlan = seatPlanService.getSeatPlanByProjectId(projectId);
        if (seatPlan == null) {
            return "redirect:/project/" + projectId;
        }
        
        // Get all guests and filter out those already assigned to chairs
        List<Guest> allGuests = guestService.getGuestsByProject(project);
        List<Guest> unassignedGuests = allGuests.stream()
            .filter(guest -> !seatPlan.getTables().stream()
                .flatMap(table -> table.getChairs().stream())
                .anyMatch(chair -> guest.equals(chair.getGuest())))
            .collect(Collectors.toList());
        
        model.addAttribute("project", project);
        model.addAttribute("seatPlan", seatPlan);
        model.addAttribute("guests", unassignedGuests);
        return "seat-plan";
    }

    @PostMapping("/assign-guest")
    @ResponseBody
    public ResponseEntity<Chair> assignGuestToChair(
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
            
            // Ensure the guest is properly loaded in the response
            chair.getGuest(); // This will trigger lazy loading if needed
            
            return ResponseEntity.ok(chair);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/remove-guest")
    @ResponseBody
    public ResponseEntity<Chair> removeGuestFromChair(
            @PathVariable Integer projectId,
            @RequestParam Integer chairId) {
        try {
            chairService.removeGuestFromChair(chairId);
            Chair chair = chairService.getChairById(chairId);
            return ResponseEntity.ok(chair);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/add-table")
    @ResponseBody
    public ResponseEntity<WeddingTable> addTable(
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
        
        return ResponseEntity.ok(table);
    }

    @PostMapping("/update-positions")
    @ResponseBody
    public ResponseEntity<String> updateTablePositions(
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

    @PutMapping("/update-table/{tableId}")
    @ResponseBody
    public ResponseEntity<WeddingTable> updateTable(
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

    @DeleteMapping("/delete-table/{tableId}")
    @ResponseBody
    public ResponseEntity<Void> deleteTable(
            @PathVariable Integer projectId,
            @PathVariable Integer tableId) {
        SeatPlan seatPlan = seatPlanService.getSeatPlanByProjectId(projectId);
        
        // Remove the table
        seatPlan.getTables().removeIf(table -> table.getId().equals(tableId));
        seatPlanService.saveSeatPlan(seatPlan);
        
        return ResponseEntity.ok().build();
    }
} 