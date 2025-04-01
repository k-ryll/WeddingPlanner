package com.example.wedding.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.wedding.model.Project;
import com.example.wedding.model.User;
import com.example.wedding.repository.ProjectRepository;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;  

    public boolean isUserInProject(String email) {
        List<Project> allProjects = projectRepository.findAll();
        return allProjects.stream()
                .anyMatch(project -> 
                    (project.getGroom() != null && project.getGroom().getEmail().equals(email)) ||
                    (project.getBride() != null && project.getBride().getEmail().equals(email)) ||
                    (project.getOrganizer() != null && project.getOrganizer().getEmail().equals(email))
                );
    }

    @Transactional
    public Project createProject(String projectName, String groomEmail, String brideEmail, String organizerEmail, String weddingDate, String status) {
        // Check if any of the users are already in a project
        if (isUserInProject(groomEmail)) {
            throw new IllegalArgumentException("Groom is already in a project!");
        }
        if (isUserInProject(brideEmail)) {
            throw new IllegalArgumentException("Bride is already in a project!");
        }
        if (organizerEmail != null && isUserInProject(organizerEmail)) {
            throw new IllegalArgumentException("Organizer is already in a project!");
        }

        User groom = userService.findByEmail(groomEmail);
        User bride = userService.findByEmail(brideEmail);
        User organizer = organizerEmail != null ? userService.findByEmail(organizerEmail) : null;

        if (groom == null || bride == null) {
            throw new IllegalArgumentException("Groom and Bride must be valid users!");
        }

        Project project = new Project();
        project.setProjectName(projectName);
        project.setGroom(groom);
        project.setBride(bride);
        project.setOrganizer(organizer);
        project.setWeddingDate(java.time.LocalDateTime.parse(weddingDate)); 
        project.setStatus(status);

        return projectRepository.save(project);
    }

    public Project findProjectByUserEmail(String email) {
        List<Project> allProjects = projectRepository.findAll();
        return allProjects.stream()
                .filter(project -> 
                    (project.getGroom() != null && project.getGroom().getEmail().equals(email)) ||
                    (project.getBride() != null && project.getBride().getEmail().equals(email)) ||
                    (project.getOrganizer() != null && project.getOrganizer().getEmail().equals(email))
                )
                .findFirst()
                .orElse(null);
    }
    
    public List<Project> findAll() {
        return projectRepository.findAll();
    }
    
    @Scheduled(fixedRate = 3600000) 
    public void changeStatus() {
    	List<Project> allProjects = projectRepository.findAll();
    	for (Project proj : allProjects) {
    	    if (proj.getWeddingDate() != null) { 
    	        LocalDate weddingDate = proj.getWeddingDate().toLocalDate();
    	        LocalDate today = LocalDate.now();
    	        
    	        if (weddingDate.isEqual(today)) {
    	            proj.setStatus("Ongoing");
    	            projectRepository.save(proj);
    	        } else if (weddingDate.isAfter(today)) { 
    	            proj.setStatus("Upcoming");
    	            projectRepository.save(proj);
    	        } else { 
    	            proj.setStatus("Completed");
    	            projectRepository.save(proj);
    	        }
    	    }
    	}

    }
}
