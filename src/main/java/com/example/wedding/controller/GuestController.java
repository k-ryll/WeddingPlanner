package com.example.wedding.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.wedding.model.Guest;
import com.example.wedding.service.GuestService;

@Controller
public class GuestController {
	
	@Autowired
	private GuestService service;
	
	@GetMapping("/guests")
	public String showGuestPage(Model model ) {
		 if (!model.containsAttribute("guest")) {
		        model.addAttribute("guest", new Guest());
		    }
		return "guestsPage";
	}
	
	@PostMapping("/guest/create")
	public String createGuest(Guest guest,
	                          RedirectAttributes redirectAttributes) {
	    service.save(guest);
	    redirectAttributes.addFlashAttribute("message", "Guest has been saved!");
	    return "redirect:/guests";
	}

}
