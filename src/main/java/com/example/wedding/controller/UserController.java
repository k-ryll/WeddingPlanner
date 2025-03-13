package com.example.wedding.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.wedding.dto.UserDto;
import com.example.wedding.model.User;
import com.example.wedding.service.DuplicateEmailException;
import com.example.wedding.service.EmailService;
import com.example.wedding.service.UserService;
import com.example.wedding.service.VerificationService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationService verificationService;

    @GetMapping("/home")
    public String showHomePage() {
        return "home";
    }

    @GetMapping("/user/login")
    public String showLoginPage(Model model) {
    	model.addAttribute("userDto", new UserDto());  
        return "Login"; 
    }

    @GetMapping("/user/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }
    

@PostMapping("/user/validate")
public String loginUser(
        @RequestParam("email") String email, 
        @RequestParam("password") String password, 
        RedirectAttributes redi, 
        HttpSession session) {

    User user = service.findByEmail(email);

    if (user != null && BCrypt.checkpw(password, user.getPassword())) {
        session.setAttribute("loggedUser", user);
        return "redirect:/home";
    } else {
        redi.addFlashAttribute("error", "Invalid email or password.");
        return "redirect:/user/login";
    }
}
    

    @PostMapping("/user/save")
    public String saveUser(@RequestParam("password") String password,
                           @RequestParam("confirmPass") String confirmPass,
                           @RequestParam("weddingDate") String weddingDateTime,
                           @RequestParam("email") String email,
                           @ModelAttribute User user,
                           RedirectAttributes redi) {
        try {
         
            if (!password.equals(confirmPass)) {
                redi.addFlashAttribute("error", "Passwords do not match!");
                redi.addFlashAttribute("user", user);
                return "redirect:/user/signup";
            }

          
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime enteredDateTime = LocalDateTime.parse(weddingDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

            if (enteredDateTime.isBefore(now)) {
                redi.addFlashAttribute("error", "Wedding date & time cannot be in the past!");
                redi.addFlashAttribute("user", user);
                return "redirect:/user/signup";
            }

         
            service.save(user);

        
            String verificationCode = verificationService.generateVerificationCode(user);

        
            try {
                emailService.sendVerificationEmail(email, verificationCode);
            } catch (Exception e) {
                redi.addFlashAttribute("error", "Failed to send verification email. Please try again.");
                return "redirect:/user/signup";
            }
            redi.addFlashAttribute("message", "A verification code has been sent to your email. Please verify your account.");
            return "redirect:/verify?email=" + email;

        } catch (DuplicateEmailException e) {
            redi.addFlashAttribute("error", e.getMessage());
            redi.addFlashAttribute("user", user);
            return "redirect:/user/signup";
        }
    }
    
}
