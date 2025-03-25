package com.example.wedding.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.wedding.model.Project;
import com.example.wedding.model.User;
import com.example.wedding.service.ProjectService;
import com.example.wedding.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
  @Autowired
    private UserService userService;
@Autowired
    private ProjectService projectService;
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
	public String showAdminHome( Model model) {
	    

	    model.addAttribute("project", new Project());
	    model.addAttribute("brides", userService.getBrides());  // Fetch brides
	    model.addAttribute("grooms", userService.getGrooms());  // Fetch grooms
	    model.addAttribute("organizers", userService.getOrganizers());  // Fetch organizers
	    return "adminDashboard";  
	}

	

	@PostMapping("/project/create")
    public String createProject(
        @RequestParam String projectName,
        @RequestParam String groomEmail,
        @RequestParam String brideEmail,
        @RequestParam(required = false) String organizerEmail,
        @RequestParam String weddingDate,
        @RequestParam String status
    ) {
		System.out.println("Creating project: " + projectName);
	    Project project = projectService.createProject(projectName, groomEmail, brideEmail, organizerEmail, weddingDate, status);
	    System.out.println("Project created successfully: " + project);
        return "redirect:/admin/dashboard";  
    }
}
