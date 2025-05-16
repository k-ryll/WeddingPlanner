package com.example.wedding.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.wedding.model.User;
import com.example.wedding.model.Vendor;
import com.example.wedding.service.VendorService;

import jakarta.servlet.http.HttpSession;

@Controller
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @GetMapping("/vendors")
    public String showVendorsPage(@SessionAttribute(name = "loggedUser", required = false) User user, Model model) {
        if (user == null) {
            return "redirect:/user/login";
        }
        
        // Get all vendors
        List<Vendor> vendors = vendorService.getAllVendors();
        model.addAttribute("vendors", vendors);
        
        // Basic user can only view vendors, not add them
        model.addAttribute("isAdmin", false);
        
        return "user_vendors";
    }
    
    @GetMapping("/admin/vendors")
    public String showAdminVendorsPage(HttpSession session, Model model) {
        try {
            System.out.println("Starting /admin/vendors endpoint");
            
            // Get all vendors
            try {
                List<Vendor> vendors = vendorService.getAllVendors();
                System.out.println("Retrieved " + vendors.size() + " vendors");
                model.addAttribute("vendors", vendors);
            } catch (Exception e) {
                System.err.println("Error getting all vendors: " + e.getMessage());
                e.printStackTrace();
                model.addAttribute("error", "Failed to load vendors: " + e.getMessage());
            }
            
            // Get vendor categories
            try {
                List<String> vendorCategories = vendorService.getDistinctVendorCategories();
                System.out.println("Retrieved " + vendorCategories.size() + " vendor categories");
                model.addAttribute("vendorCategories", vendorCategories);
            } catch (Exception e) {
                System.err.println("Error getting vendor categories: " + e.getMessage());
                e.printStackTrace();
                model.addAttribute("error", "Failed to load vendor categories: " + e.getMessage());
            }
            
            // Create new vendor model for the form
            try {
                Vendor newVendor = new Vendor();
                model.addAttribute("newVendor", newVendor);
            } catch (Exception e) {
                System.err.println("Error creating new vendor: " + e.getMessage());
                e.printStackTrace();
                model.addAttribute("error", "Failed to create new vendor form: " + e.getMessage());
            }
            
            // Always set isAdmin to true for this test
            model.addAttribute("isAdmin", true);
            
            System.out.println("Admin vendors page prepared successfully");
            return "admin_vendors";
        } catch (Exception e) {
            System.err.println("Unhandled exception in showAdminVendorsPage: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "admin_vendors";
        }
    }
    
    @PostMapping("/admin/vendors/add")
    public String addVendor(HttpSession session,
                           @ModelAttribute Vendor vendor,
                           @RequestParam(name = "categoryNames") String categoryNames,
                           RedirectAttributes redirectAttributes) {
        
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/admin/login";
        }
        
        try {
            // Validate categories are not empty
            if (categoryNames == null || categoryNames.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "At least one category is required");
                return "redirect:/admin/vendors";
            }
            
            // Parse comma-separated categories
            List<String> categoryList = Arrays.asList(categoryNames.split("\\s*,\\s*"));
            
            // Validate list is not empty (handles edge cases like only spaces or commas)
            if (categoryList.isEmpty() || (categoryList.size() == 1 && categoryList.get(0).isEmpty())) {
                redirectAttributes.addFlashAttribute("error", "At least one category is required");
                return "redirect:/admin/vendors";
            }
            
            vendorService.saveVendor(vendor, categoryList);
            redirectAttributes.addFlashAttribute("message", "Vendor added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding vendor: " + e.getMessage());
        }
        
        return "redirect:/admin/vendors";
    }
    
    @PostMapping("/admin/vendors/{id}/delete")
    public String deleteVendor(HttpSession session,
                             @PathVariable Integer id,
                             RedirectAttributes redirectAttributes) {
        
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/admin/login";
        }
        
        try {
            vendorService.deleteVendor(id);
            redirectAttributes.addFlashAttribute("message", "Vendor deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting vendor: " + e.getMessage());
        }
        
        return "redirect:/admin/vendors";
    }
    
    @PostMapping("/admin/vendors/{id}/edit")
    public String editVendor(HttpSession session,
                           @PathVariable Integer id,
                           @RequestParam String name,
                           @RequestParam String categoryNames,
                           @RequestParam(required = false) String location,
                           @RequestParam String priceType,
                           @RequestParam(required = false) BigDecimal pricePerGuest,
                           @RequestParam(required = false) BigDecimal totalPrice,
                           @RequestParam(required = false) String contact,
                           @RequestParam(required = false) String description,
                           RedirectAttributes redirectAttributes) {
        
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/admin/login";
        }
        
        try {
            // Validate categories are not empty
            if (categoryNames == null || categoryNames.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "At least one category is required");
                return "redirect:/admin/vendors";
            }
            
            Vendor vendor = vendorService.getVendorById(id);
            if (vendor == null) {
                redirectAttributes.addFlashAttribute("error", "Vendor not found!");
                return "redirect:/admin/vendors";
            }
            
            vendor.setName(name);
            vendor.setLocation(location);
            vendor.setPriceType(priceType);
            vendor.setPricePerGuest(pricePerGuest);
            vendor.setTotalPrice(totalPrice);
            vendor.setContact(contact);
            vendor.setDescription(description);
            
            // Parse comma-separated categories
            List<String> categoryList = Arrays.asList(categoryNames.split("\\s*,\\s*"));
            
            // Validate list is not empty (handles edge cases like only spaces or commas)
            if (categoryList.isEmpty() || (categoryList.size() == 1 && categoryList.get(0).isEmpty())) {
                redirectAttributes.addFlashAttribute("error", "At least one category is required");
                return "redirect:/admin/vendors";
            }
            
            vendorService.saveVendor(vendor, categoryList);
            
            redirectAttributes.addFlashAttribute("message", "Vendor updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating vendor: " + e.getMessage());
        }
        
        return "redirect:/admin/vendors";
    }

    @GetMapping("/admin/vendors-simple")
    public String showSimpleAdminVendorsPage(Model model) {
        try {
            System.out.println("Starting /admin/vendors-simple endpoint");
            
            // Just get the list of vendors
            try {
                List<Vendor> vendors = vendorService.getAllVendors();
                System.out.println("Retrieved " + vendors.size() + " vendors");
                model.addAttribute("vendors", vendors);
            } catch (Exception e) {
                System.err.println("Error getting vendors: " + e.getMessage());
                e.printStackTrace();
                model.addAttribute("error", "Failed to load vendors: " + e.getMessage());
            }
            
            return "admin_vendors_simple";
        } catch (Exception e) {
            System.err.println("Unhandled exception: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "admin_vendors_simple";
        }
    }
} 