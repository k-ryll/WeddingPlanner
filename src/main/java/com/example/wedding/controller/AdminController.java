package com.example.wedding.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {
	@GetMapping("/admin/login")
	public String showAdminLogin() {
		return "adminLogin";
	}
	
	@PostMapping("/admin/validate")
	public String validateAdmin(
			@RequestParam("email") String email,
			@RequestParam("password") String password,
			RedirectAttributes redi) {
		if(email.equals("admin") && password.equals("password")) {
			return "redirect:/admin/home";
		}
		return "redirect:/admin/login";
		
	}
	
	@GetMapping("/admin/home")
	public String showAdminHome() {
		return "adminDashboard";
	}
}
