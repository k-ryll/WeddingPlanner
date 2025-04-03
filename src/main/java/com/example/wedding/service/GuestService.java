package com.example.wedding.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            return repo.findByProjectId(userProject);
        }
        // If user is not in a project, return only guests they added
        return repo.findByAddedBy(user);
    }
	
	public String save(Guest guest) {
		repo.save(guest);
		return "redirect:/home";
	}

    public Guest findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public Guest findById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    @Transactional
    public Guest updateGuest(Integer guestId, String title, String name, String email, 
            String entourage, String rsvp) {
        Guest guest = findById(guestId);
        if (guest == null) {
            throw new IllegalArgumentException("Guest not found");
        }

        guest.setTitle(title);
        guest.setName(name);
        guest.setEmail(email);
        guest.setEntourage(entourage);
        guest.setRsvp(rsvp);

        return repo.save(guest);
    }

    @Transactional
    public void deleteGuest(Integer guestId) {
        Guest guest = findById(guestId);
        if (guest == null) {
            throw new IllegalArgumentException("Guest not found");
        }
        repo.delete(guest);
    }
}
