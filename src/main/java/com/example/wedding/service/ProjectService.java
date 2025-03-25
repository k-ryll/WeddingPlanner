package com.example.wedding.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.wedding.model.Project;
import com.example.wedding.model.User;
import com.example.wedding.repository.ProjectRepository;
import jakarta.transaction.Transactional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;  

    @Transactional
    public Project createProject(String projectName, String groomEmail, String brideEmail, String organizerEmail, String weddingDate, String status) {
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
}
