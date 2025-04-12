package com.example.wedding.service;

import com.example.wedding.model.ItineraryItem;
import com.example.wedding.model.Project;
import com.example.wedding.repository.ItineraryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItineraryService {
    @Autowired
    private ItineraryItemRepository itineraryItemRepository;

    public List<ItineraryItem> findByProject(Project project) {
        return itineraryItemRepository.findByProjectOrderByStartTimeAsc(project);
    }

    public ItineraryItem save(ItineraryItem item) {
        return itineraryItemRepository.save(item);
    }

    public void delete(Integer id) {
        itineraryItemRepository.deleteById(id);
    }

    public ItineraryItem findById(Integer id) {
        return itineraryItemRepository.findById(id).orElse(null);
    }
} 