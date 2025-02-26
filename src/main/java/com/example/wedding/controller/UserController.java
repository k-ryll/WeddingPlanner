package com.example.wedding.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.example.wedding.model.User;

import com.example.wedding.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService service;
	
	private User user;
	

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
	public String saveUser(User user, RedirectAttributes redi) {
		
		redi.addFlashAttribute("message", "User has been saved!");
		service.save(user);
		return "redirect:/home";
		
	}
	
	@PostMapping("/user/validate")
	public String validateUser(@RequestParam("email") String email,
			@RequestParam("password") String password, Model model)  {
		String userEmail = service.findByEmail();
		return "redirect:/home";
	}
	
	
}
