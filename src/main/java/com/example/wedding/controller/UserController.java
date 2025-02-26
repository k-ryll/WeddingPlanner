package com.example.wedding.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.example.wedding.model.User;
import com.example.wedding.service.DuplicateEmailException;
import com.example.wedding.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService service;
	

	

	@GetMapping("/home")
	public String showHomePage() {
		return "home";
	}
	
	@GetMapping("/user/login")
	public String showLoginPage() {
		return "login";
	}
	@GetMapping("/user/signup")
	public String showSignupPage(Model model) {
		model.addAttribute("user",new User());
		return "signup";
	}
	
	@PostMapping("/user/save")
	public String saveUser( User user, RedirectAttributes redi) {
        try {
            service.save(user);
            redi.addFlashAttribute("message", "You have successfully registered to Fitrack! Login to your account now.");
            return "redirect:/home";
        }catch(DuplicateEmailException e) {
            redi.addFlashAttribute("error", e.getMessage());
            redi.addFlashAttribute("user", user);
            return "redirect:/user/signup";
        }
    }
	
	
	
	
}
