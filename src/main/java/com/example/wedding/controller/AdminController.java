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
import com.example.wedding.service.VendorService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProjectService projectService;

    @Autowired
    private VendorService vendorService;

    @GetMapping("/admin/login")
    public String showAdminLogin() {
        return "adminLogin";
    }
    
    @PostMapping("/admin/validate")
    public String validateAdmin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            RedirectAttributes redi, HttpSession session) {
        System.out.println("Admin login attempt: " + email);
        
        // Admin authentication without database user
        if(email.equals("admin") && password.equals("password")) {
            System.out.println("Admin login successful, setting session attribute");
            session.setAttribute("isAdmin", true);
            return "redirect:/admin/home";
        }
        
        System.out.println("Admin login failed");
        redi.addFlashAttribute("error", "Invalid admin credentials.");
        return "redirect:/admin/login";
    }
    
    @GetMapping("/admin/home")
    public String showAdminHome(HttpSession session, Model model) {
        // Check if admin is logged in
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("project", new Project());
        model.addAttribute("brides", userService.getBrides());
        model.addAttribute("grooms", userService.getGrooms());
        model.addAttribute("organizers", userService.getOrganizers());
        model.addAttribute("projects", projectService.findAll());
        return "adminDashboard";  
    }

    @GetMapping("/admin/users")
    public String showAdminUsers(Model model, HttpSession session) {
        // Basic check if admin is logged in (improve security as needed)
        if (session.getAttribute("isAdmin") == null || !(Boolean)session.getAttribute("isAdmin")) {
            return "redirect:/admin/login";
        }
        List<User> users = userService.findAllUsers(); // Assuming userService has a method to find all users
        model.addAttribute("users", users);
        return "adminUsers";
    }

    @PostMapping("/project/create")
    public String createProject(
        @RequestParam String projectName,
        @RequestParam String groomEmail,
        @RequestParam String brideEmail,
        @RequestParam(required = false) String organizerEmail,
        @RequestParam String weddingDate,
        @RequestParam String status,
        RedirectAttributes redirectAttributes
    ) {
        try {
            System.out.println("Creating project: " + projectName);
            Project project = projectService.createProject(projectName, groomEmail, brideEmail, organizerEmail, weddingDate, status);
            System.out.println("Project created successfully: " + project);
            redirectAttributes.addFlashAttribute("success", "Project created successfully!");
            return "redirect:/admin/home";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/home";
        }
    }

    @PostMapping("/project/edit")
    public String editProject(
        @RequestParam Integer projectId,
        @RequestParam String projectName,
        @RequestParam String groomEmail,
        @RequestParam String brideEmail,
        @RequestParam(required = false) String organizerEmail,
        @RequestParam String weddingDate,
        @RequestParam String status,
        RedirectAttributes redirectAttributes
    ) {
        try {
            Project project = projectService.findById(projectId);
            if (project == null) {
                redirectAttributes.addFlashAttribute("error", "Project not found!");
                return "redirect:/admin/home";
            }
            projectService.updateProject(projectId, projectName, groomEmail, brideEmail, organizerEmail, weddingDate, status);
            redirectAttributes.addFlashAttribute("success", "Project updated successfully!");
            return "redirect:/admin/home";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/home";
        }
    }

    @PostMapping("/project/delete")
    public String deleteProject(
        @RequestParam Integer projectId,
        RedirectAttributes redirectAttributes
    ) {
        try {
            projectService.deleteProject(projectId);
            redirectAttributes.addFlashAttribute("success", "Project deleted successfully!");
            return "redirect:/admin/home";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/home";
        }
    }

    @GetMapping("/admin/logout")
    public String adminLogout(HttpSession session) {
        session.removeAttribute("isAdmin");
        return "redirect:/admin/login";
    }

    @GetMapping("/admin/session-test")
    public String testAdminSession(HttpSession session, Model model) {
        // Log and display session attributes
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        System.out.println("Admin session test: isAdmin = " + isAdmin);
        
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("sessionId", session.getId());
        model.addAttribute("allAttributes", session.getAttributeNames());
        
        // Add database connectivity check
        try {
            // Attempt to get vendor count to check database connectivity
            long vendorCount = vendorService.getAllVendors().size();
            model.addAttribute("dbStatus", "Connected");
            model.addAttribute("vendorCount", vendorCount);
            System.out.println("Database check successful. Found " + vendorCount + " vendors.");
        } catch (Exception e) {
            model.addAttribute("dbStatus", "Error: " + e.getMessage());
            model.addAttribute("vendorCount", 0);
            System.err.println("Database check failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "admin_session_test";
    }
}
