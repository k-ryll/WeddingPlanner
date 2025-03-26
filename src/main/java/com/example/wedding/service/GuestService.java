package com.example.wedding.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.wedding.model.Guest;
import com.example.wedding.model.Project;
import com.example.wedding.model.User;
import com.example.wedding.repository.GuestRepository;


@Service
public class GuestService {
	@Autowired
	private GuestRepository repo;
	
	@Autowired
	private ProjectService projectService;
	

    public List<Guest> findByUser(User user) {
        // Get the user's project
        Project userProject = projectService.findProjectByUserEmail(user.getEmail());
        if (userProject != null) {
            // If user is in a project, return all guests in that project
            return repo.findByProjectId(userProject);
        }
        // If user is not in a project, return only guests they added
        return repo.findByAddedBy(user);
    }
	
	public String save(Guest guest) {
		repo.save(guest);
		return "redirect:/home";
	}
}
