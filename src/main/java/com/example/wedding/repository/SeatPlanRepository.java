package com.example.wedding.repository;

import com.example.wedding.model.SeatPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatPlanRepository extends JpaRepository<SeatPlan, Integer> {
    SeatPlan findByProjectId(Integer projectId);
} 