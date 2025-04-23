package com.example.wedding.controller;

import com.example.wedding.model.Project;
import com.example.wedding.model.SeatPlan;
import com.example.wedding.model.WeddingTable;
import com.example.wedding.service.ProjectService;
import com.example.wedding.service.SeatPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/project/{projectId}/seatplan")
public class SeatPlanController {
    @Autowired
    private SeatPlanService seatPlanService;
    
    @Autowired
    private ProjectService projectService;

    @GetMapping
    public String getSeatPlan(@PathVariable Integer projectId, Model model) {
        Project project = projectService.findById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Project not found");
        }
        
        SeatPlan seatPlan = seatPlanService.getSeatPlanByProjectId(projectId);
        if (seatPlan == null) {
            seatPlan = seatPlanService.createSeatPlan(project);
        }
        
        model.addAttribute("project", project);
        model.addAttribute("seatPlan", seatPlan);
        return "seat-plan";
    }

    @PostMapping("/add-table")
    @ResponseBody
    public ResponseEntity<WeddingTable> addTable(
            @PathVariable Integer projectId,
            @RequestBody WeddingTable table) {
        SeatPlan seatPlan = seatPlanService.getSeatPlanByProjectId(projectId);
        table.setSeatPlan(seatPlan);
        seatPlan.getTables().add(table);
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
} 