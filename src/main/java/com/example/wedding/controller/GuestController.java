package com.example.wedding.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.wedding.model.Guest;
import com.example.wedding.model.Project;
import com.example.wedding.model.User;
import com.example.wedding.service.GuestService;
import com.example.wedding.service.ProjectService;
import com.example.wedding.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
public class GuestController {
	
	@Autowired
	private GuestService service;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private EmailService emailService;
	
	@GetMapping("/guests")
	public String showGuestPage(Model model, HttpSession session) {
	    User loggedUser = (User) session.getAttribute("loggedUser");

	    if (loggedUser == null) {
	        return "redirect:/user/login"; // Redirect to login if not logged in
	    }

	    List<Guest> guests = service.findByUser(loggedUser); // Fetch guests for the logged-in user
	    model.addAttribute("guests", guests); // Add guest list to model

	    if (!model.containsAttribute("guest")) {
	        model.addAttribute("guest", new Guest());
	    }

	    return "guestsPage";
	}

	
	@PostMapping("/guest/create")
	public String createGuest(Guest guest, HttpSession session, RedirectAttributes redirectAttributes) {
	    // Retrieve the logged-in user from session
	    User loggedUser = (User) session.getAttribute("loggedUser");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

        if (loggedUser == null && !Boolean.TRUE.equals(isAdmin)) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to add a guest.");
            return "redirect:/user/login";
        }

	    // Find the project associated with the logged-in user
	    Project userProject = projectService.findProjectByUserEmail(loggedUser.getEmail());
	    
	    if (userProject == null) {
	        redirectAttributes.addFlashAttribute("error", "You must be part of a project to add guests.");
	        return "redirect:/guests";
	    }

	    // Associate the guest with the logged-in user and their project
	    guest.setAddedBy(loggedUser);
	    guest.setProjectId(userProject);
	    
	    // Set default RSVP status
	    guest.setRsvp("Pending");

	    // Save the guest
	    service.save(guest);

	    redirectAttributes.addFlashAttribute("message", "Guest has been saved!");
	    return "redirect:/guests";
	}

    @PostMapping("/guest/send-invitation")
    public ResponseEntity<?> sendInvitation(@RequestParam String email) {
        try {
            Guest guest = service.findByEmail(email);
            if (guest == null) {
                return ResponseEntity.badRequest().body("Guest not found");
            }
            
            emailService.sendInvitationEmail(
                guest.getEmail(),
                guest.getName(),
                guest.getProjectId().getProjectName()
            );
            
            return ResponseEntity.ok("Invitation sent successfully");
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError().body("Failed to send invitation: " + e.getMessage());
        }
    }

    @GetMapping("/guest/rsvp")
    public String handleRsvp(
            @RequestParam String email,
            @RequestParam String response,
            RedirectAttributes redirectAttributes) {
        
        Guest guest = service.findByEmail(email);
        if (guest == null) {
            redirectAttributes.addFlashAttribute("error", "Guest not found");
            return "redirect:/rsvp-error";
        }

        guest.setRsvp(response.equals("accept") ? "Accepted" : "Declined");
        guest.setRemarks(response.equals("accept") ? "Accepted via email" : "Declined via email");
        service.save(guest);

        redirectAttributes.addFlashAttribute("message", 
            "Thank you for your response! Your RSVP has been recorded as: " + guest.getRsvp());
        return "redirect:/index";
    }

    @PostMapping("/guest/edit")
    public String editGuest(
        @RequestParam Integer guestId,
        @RequestParam String title,
        @RequestParam String name,
        @RequestParam String email,
        @RequestParam String entourage,
        @RequestParam String rsvp,
        @RequestParam(required = false) String remarks,
        RedirectAttributes redirectAttributes
    ) {
        try {
            Guest guest = service.findById(guestId);
            if (guest == null) {
                redirectAttributes.addFlashAttribute("error", "Guest not found!");
                return "redirect:/guests";
            }
            service.updateGuest(guestId, title, name, email, entourage, rsvp, remarks);
            redirectAttributes.addFlashAttribute("message", "Guest updated successfully!");
            return "redirect:/guests";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/guests";
        }
    }
    
    // New method for AJAX-based guest editing with detailed error reporting
    @PostMapping("/guest/edit-ajax")
    @ResponseBody
    public String editGuestAjax(
        @RequestParam(required = false) Integer guestId,
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String entourage,
        @RequestParam(required = false) String rsvp,
        @RequestParam(required = false) String remarks
    ) {
        try {
            if (guestId == null) {
                return "{\"success\": false, \"error\": \"Missing guestId parameter\"}";
            }
            
            Guest guest = service.findById(guestId);
            if (guest == null) {
                return "{\"success\": false, \"error\": \"Guest not found: " + guestId + "\"}";
            }
            
            // Check required fields
            if (title == null || title.isEmpty()) {
                return "{\"success\": false, \"error\": \"Missing title parameter\"}";
            }
            if (name == null || name.isEmpty()) {
                return "{\"success\": false, \"error\": \"Missing name parameter\"}";
            }
            if (email == null || email.isEmpty()) {
                return "{\"success\": false, \"error\": \"Missing email parameter\"}";
            }
            if (entourage == null || entourage.isEmpty()) {
                return "{\"success\": false, \"error\": \"Missing entourage parameter\"}";
            }
            if (rsvp == null || rsvp.isEmpty()) {
                return "{\"success\": false, \"error\": \"Missing rsvp parameter\"}";
            }
            
            service.updateGuest(guestId, title, name, email, entourage, rsvp, remarks);
            return "{\"success\": true, \"message\": \"Guest updated successfully!\"}";
        } catch (Exception e) {
            return "{\"success\": false, \"error\": \"" + e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    @PostMapping("/guest/delete")
    public String deleteGuest(
        @RequestParam Integer guestId,
        RedirectAttributes redirectAttributes
    ) {
        try {
            service.deleteGuest(guestId);
            redirectAttributes.addFlashAttribute("message", "Guest deleted successfully!");
            return "redirect:/guests";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/guests";
        }
    }

    @PostMapping("/guests/send-all-invitations")
    public String sendAllInvitations(HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to send invitations.");
            return "redirect:/user/login";
        }

        Project userProject = projectService.findProjectByUserEmail(loggedUser.getEmail());
        if (userProject == null) {
            redirectAttributes.addFlashAttribute("error", "You must be part of a project to send invitations.");
            return "redirect:/guests";
        }

        List<Guest> guests = service.findByUser(loggedUser);
        if (guests.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No guests found to send invitations to.");
            return "redirect:/guests";
        }

        try {
            emailService.sendBulkInvitations(guests, userProject.getProjectName());
            redirectAttributes.addFlashAttribute("message", "Invitations sent successfully to all guests!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send some invitations: " + e.getMessage());
        }

        return "redirect:/guests";
    }
}
