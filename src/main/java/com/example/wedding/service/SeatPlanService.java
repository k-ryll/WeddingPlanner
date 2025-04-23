package com.example.wedding.service;

import com.example.wedding.model.Project;
import com.example.wedding.model.SeatPlan;
import com.example.wedding.model.WeddingTable;
import com.example.wedding.repository.SeatPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatPlanService {
    @Autowired
    private SeatPlanRepository seatPlanRepository;

    public SeatPlan getSeatPlanByProjectId(Integer projectId) {
        return seatPlanRepository.findByProjectId(projectId);
    }

    @Transactional
    public SeatPlan createSeatPlan(Project project) {
        SeatPlan seatPlan = new SeatPlan(project);
        return seatPlanRepository.save(seatPlan);
    }

    @Transactional
    public SeatPlan saveSeatPlan(SeatPlan seatPlan) {
        return seatPlanRepository.save(seatPlan);
    }

    @Transactional
    public void updateTablePositions(Integer seatPlanId, List<WeddingTable> tables) {
        SeatPlan seatPlan = seatPlanRepository.findById(seatPlanId)
                .orElseThrow(() -> new RuntimeException("Seat plan not found"));
        
        seatPlan.setTables(tables);
        seatPlanRepository.save(seatPlan);
    }
} 