package com.example.wedding.repository;

import com.example.wedding.model.ItineraryItem;
import com.example.wedding.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItineraryItemRepository extends JpaRepository<ItineraryItem, Integer> {
    List<ItineraryItem> findByProjectOrderByStartTimeAsc(Project project);
} 