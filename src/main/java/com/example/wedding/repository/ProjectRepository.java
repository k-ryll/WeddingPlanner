package com.example.wedding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.wedding.model.Project;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    
    // Find projects by organizer
    List<Project> findByOrganizerEmail(String email);
    
    // Find projects by status (e.g., "Pending", "Confirmed")
    List<Project> findByStatus(String status);
}
