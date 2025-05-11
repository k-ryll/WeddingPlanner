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
	
	@Autowired
	private UserService userService;
	

    public List<Guest> findByUser(User user) {
        // Get the user's project
        Project userProject = projectService.findProjectByUserEmail(user.getEmail());
        if (userProject != null) {
            return repo.findByProjectId(userProject);
        }
        // If user is not in a project, return only guests they added
        return repo.findByAddedBy(user);
    }
    
    public List<Guest> findByProject(Project project) {
        return repo.findByProjectId(project);
    }
    
	
	public String save(Guest guest) {
		// Check if trying to add Best Man or Maid of Honor
		if ("Best Man".equals(guest.getEntourage()) || "Maid of Honor".equals(guest.getEntourage())) {
			// Check if one already exists
			List<Guest> existingGuests = repo.findByProjectId(guest.getProjectId());
			boolean exists = existingGuests.stream()
				.anyMatch(g -> g.getEntourage().equals(guest.getEntourage()));
			
			if (exists) {
				throw new IllegalArgumentException("A " + guest.getEntourage() + " already exists for this project.");
			}

			// Get the project and update it with the new best man or maid of honor
			Project project = guest.getProjectId();
			User user = userService.findByEmail(guest.getEmail());
			if (user == null) {
				throw new IllegalArgumentException("User not found for email: " + guest.getEmail());
			}

			if ("Best Man".equals(guest.getEntourage())) {
				project.setBestMan(user);
			} else if ("Maid of Honor".equals(guest.getEntourage())) {
				project.setMaidOfHonor(user);
			}
			projectService.updateProject(
				project.getId(),
				project.getProjectName(),
				project.getGroom().getEmail(),
				project.getBride().getEmail(),
				project.getOrganizer() != null ? project.getOrganizer().getEmail() : null,
				project.getWeddingDate().toString(),
				project.getStatus()
			);
		}
		
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
            String entourage, String rsvp, String remarks) {
        Guest guest = findById(guestId);
        if (guest == null) {
            throw new IllegalArgumentException("Guest not found");
        }

        // Check if trying to update to Best Man or Maid of Honor
        if (("Best Man".equals(entourage) || "Maid of Honor".equals(entourage)) 
            && !entourage.equals(guest.getEntourage())) {
            // Check if one already exists
            List<Guest> existingGuests = repo.findByProjectId(guest.getProjectId());
            boolean exists = existingGuests.stream()
                .anyMatch(g -> g.getEntourage().equals(entourage) && !g.getGuestId().equals(guestId));
            
            if (exists) {
                throw new IllegalArgumentException("A " + entourage + " already exists for this project.");
            }

            // Get the project and update it with the new best man or maid of honor
            Project project = guest.getProjectId();
            User user = userService.findByEmail(email);
            if (user == null) {
                throw new IllegalArgumentException("User not found for email: " + email);
            }

            if ("Best Man".equals(entourage)) {
                project.setBestMan(user);
            } else if ("Maid of Honor".equals(entourage)) {
                project.setMaidOfHonor(user);
            }
            projectService.updateProject(
                project.getId(),
                project.getProjectName(),
                project.getGroom().getEmail(),
                project.getBride().getEmail(),
                project.getOrganizer() != null ? project.getOrganizer().getEmail() : null,
                project.getWeddingDate().toString(),
                project.getStatus()
            );
        }

        guest.setTitle(title);
        guest.setName(name);
        guest.setEmail(email);
        guest.setEntourage(entourage);
        guest.setRsvp(rsvp);
        guest.setRemarks(remarks);

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

    public List<Guest> getGuestsByProject(Project project) {
        return repo.findByProjectId(project);
    }
}
