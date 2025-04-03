package com.example.wedding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.wedding.model.Project;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    
   
    List<Project> findByOrganizerEmail(String email);
    
    
    List<Project> findByStatus(String status);
}
