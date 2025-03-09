package com.example.wedding.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        model.addAttribute("user", new User());
        return "signup";
    }

 @PostMapping("/user/save")
public String saveUser(@RequestParam("password") String password,
                       @RequestParam("confirmPass") String confirmPass,
                       @RequestParam("weddingDate") String weddingDateTime,
                       @ModelAttribute User user,
                       RedirectAttributes redi) {
    try {
        // Validate password match
        if (!password.equals(confirmPass)) {
            redi.addFlashAttribute("error", "Passwords do not match!");
            redi.addFlashAttribute("user", user);
            return "redirect:/user/signup";
        }

        // Validate wedding date is not in the past
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime enteredDateTime = LocalDateTime.parse(weddingDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        if (enteredDateTime.isBefore(now)) {
            redi.addFlashAttribute("error", "Wedding date & time cannot be in the past!");
            redi.addFlashAttribute("user", user);
            return "redirect:/user/signup";
        }

        service.save(user);
        redi.addFlashAttribute("message", "You have successfully registered! Login to your account now.");
        return "redirect:/home";
    } catch (DuplicateEmailException e) {
        redi.addFlashAttribute("error", e.getMessage());
        redi.addFlashAttribute("user", user);
        return "redirect:/user/signup";
    }
}
}
