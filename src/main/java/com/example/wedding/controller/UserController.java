package com.example.wedding.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.wedding.dto.UserDto;
import com.example.wedding.model.User;
import com.example.wedding.service.DuplicateEmailException;
import com.example.wedding.service.EmailService;
import com.example.wedding.service.UserService;
import com.example.wedding.service.VerificationService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Collections;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationService verificationService;

    @GetMapping("/favicon.ico")
    void disableFavicon(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    @GetMapping("/index")
    public String showIndexPage() {
        
        return "index"; 
    }
    @GetMapping("/home")
    public String home(@SessionAttribute(name = "loggedUser", required = false) User user, Model model) {
        if (user == null) {
            return "redirect:/user/login";    }
        model.addAttribute("user", user);
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
        
        System.out.println("Email: " + email);
        User user = service.findByEmail(email);
        
        if (user != null) {
            System.out.println("User found: " + user.getEmail());
        } else {
            System.out.println("User not found!");
        }
        
        if (user != null && BCrypt.checkpw(password, user.getPassword()) && user.isVerified()) {
            
            session.setAttribute("loggedUser", user);
            

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user, 
                    null, 
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
            

            SecurityContextHolder.getContext().setAuthentication(auth);
            
      
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
         
            return "redirect:/home";
        } else {
            redi.addFlashAttribute("error", "Invalid email or password.");
            return "redirect:/user/login";
        }
    }

@PostMapping("/user/save")
public String saveUser(@RequestParam("password") String password,
                       @RequestParam("confirmPass") String confirmPass,
                       @RequestParam("email") String email,
                       @ModelAttribute User user,
                       RedirectAttributes redi) {
    try {
        if (!password.equals(confirmPass)) {
            redi.addFlashAttribute("error", "Passwords do not match!");
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
