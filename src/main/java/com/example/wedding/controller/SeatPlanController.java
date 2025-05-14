package com.example.wedding.controller;

import com.example.wedding.model.*;
import com.example.wedding.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    // User seat plan route
    @GetMapping("/seatplan")
    public String getUserSeatPlan(@SessionAttribute(name = "loggedUser", required = false) User user, Model model) {
        if (user == null) {
            return "redirect:/user/login";
        }
        
        Project project = projectService.findProjectByUserEmail(user.getEmail());
        
        if (project == null) {
            model.addAttribute("project", null);
            model.addAttribute("seatPlan", null); // Or new SeatPlan() if you prefer an empty object
            model.addAttribute("guests", Collections.emptyList());
            model.addAttribute("error", "No project found. Please create a project to use the seat planning tool.");
            return "user-seat-plan"; // Proceed to the page but with an error/disabled state
        }
        
        final SeatPlan seatPlan = seatPlanService.getSeatPlanByProjectId(project.getId());
        if (seatPlan == null) {
            // This case might mean a project exists but has no seat plan yet.
            // You might want to create a new SeatPlan here or handle it in the template.
            model.addAttribute("project", project);
            model.addAttribute("seatPlan", null); // Or new SeatPlan() and save it if that's the desired flow
            model.addAttribute("guests", guestService.getGuestsByProject(project)); // Still might want to show guests
            model.addAttribute("warning", "Seat plan not initialized for this project. Some features may be limited.");
            // return "user-seat-plan"; // Or redirect to a page to initialize the seat plan
        } else {
            model.addAttribute("seatPlan", seatPlan);
            // Filter out assigned guests
            List<Guest> allGuests = guestService.getGuestsByProject(project);
            List<Guest> unassignedGuests = allGuests.stream()
                .filter(guest -> !seatPlan.getTables().stream()
                    .flatMap(table -> table.getChairs().stream())
                    .anyMatch(chair -> guest.equals(chair.getGuest())))
                .collect(Collectors.toList());
            model.addAttribute("guests", unassignedGuests);
        }
        
        model.addAttribute("project", project); // Ensure project is always added if not null
        return "user-seat-plan";
    }

    // Admin seat plan route
    @GetMapping("/project/{projectId}/seatplan")
    public String getAdminSeatPlan(@PathVariable Integer projectId, Model model) {
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

    // User endpoints
    @PostMapping("/seatplan/assign-guest")
    @ResponseBody
    public ResponseEntity<Chair> assignGuestToChair(
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
            
            // Ensure the guest is properly loaded in the response
            chair.getGuest(); // This will trigger lazy loading if needed
            
            return ResponseEntity.ok(chair);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/seatplan/remove-guest")
    @ResponseBody
    public ResponseEntity<Chair> removeGuestFromChair(
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
            return ResponseEntity.ok(chair);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/seatplan/add-table")
    @ResponseBody
    public ResponseEntity<WeddingTable> addTable(
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
        
        return ResponseEntity.ok(table);
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
    public ResponseEntity<Chair> assignGuestToChairAdmin(
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

    @PostMapping("/project/{projectId}/seatplan/remove-guest")
    @ResponseBody
    public ResponseEntity<Chair> removeGuestFromChairAdmin(
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

    @PostMapping("/project/{projectId}/seatplan/add-table")
    @ResponseBody
    public ResponseEntity<WeddingTable> addTableAdmin(
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