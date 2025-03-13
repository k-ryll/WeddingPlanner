package com.example.wedding.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.wedding.model.User;
import com.example.wedding.service.EmailService;
import com.example.wedding.service.UserService;
import com.example.wedding.service.VerificationService;

@Controller
public class VerificationController {

    @Autowired
    private VerificationService verificationService;
    
    @Autowired
    private UserService service;
    
    @Autowired
    private EmailService emailService;
    
    @GetMapping("/verify")
    public String showVerificationPage(@RequestParam(name = "email", required = false) String email, Model model) {
        model.addAttribute("email", email); 
        return "verify"; 
    }


    @PostMapping("/verify")
    public String verifyUser(@RequestParam("email") String email, 
                             @RequestParam("code") String code, 
                             RedirectAttributes redirectAttributes) {
        boolean isValid = verificationService.verifyCode(email, code);
        
        if (isValid) {
            redirectAttributes.addFlashAttribute("message", "Verification successful! You can now access your account.");
            return "redirect:/home"; 
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired verification code. Please try again.");
            redirectAttributes.addFlashAttribute("email", email); 
            return "redirect:/verify"; 
        }
    }
    
    @GetMapping("/user/reset")
    public String showResetPage() {
    	return "reset";
    }
    
    @PostMapping("/user/reset-password")
    public String resetPassword(Model model, @RequestParam("email") String email, RedirectAttributes redi){
    	User user = service.findByEmail(email);
    	if (user != null && user.isVerified() == true) {
    		emailService.sendResetPassword(email);
    		return "redirect:/user/login";
    	}
    	return "redirect:/user/login";
		
    }
}
