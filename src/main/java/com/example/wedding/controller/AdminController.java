package com.example.wedding.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
	@GetMapping("/admin/login")
	public String showAdminLogin() {
		return "adminLogin";
	}
	
	@GetMapping("/admin/home")
	public String showAdminHome() {
		return "adminDashboard";
	}
}
