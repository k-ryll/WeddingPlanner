package com.example.wedding.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	    if (loggedUser == null) {
	        redirectAttributes.addFlashAttribute("error", "You must be logged in to add a guest.");
	        return "redirect:/user/login"; // Redirect to login if not logged in
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
        service.save(guest);

        redirectAttributes.addFlashAttribute("message", 
            "Thank you for your response! Your RSVP has been recorded as: " + guest.getRsvp());
        return "redirect:/index";
    }
}
