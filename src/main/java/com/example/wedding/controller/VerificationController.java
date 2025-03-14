package com.example.wedding.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.wedding.model.ResetPasswordToken;
import com.example.wedding.model.User;
import com.example.wedding.repository.ResetPasswordTokenRepository;
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
    
    @Autowired
    private ResetPasswordTokenRepository resetPasswordTokenRepository;
    
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
    		String token = verificationService.generateResetPasswordToken(user);
    		emailService.sendResetPassword(email, token);
    		 
             redi.addFlashAttribute("message", "Email sent successfully, check your email to reset password"); 
    		
             return "redirect:/user/login";
    	}
    	redi.addFlashAttribute("error", "Email doesn't exist");
    	return "redirect:/user/login";
		
    }
    @GetMapping("/user/resetpassword")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model, RedirectAttributes redi) {
        Optional<ResetPasswordToken> optionalToken = resetPasswordTokenRepository.findByToken(token);
        
        
        if (!optionalToken.isPresent() || optionalToken.get().isExpired()) {
            redi.addFlashAttribute("error", "Invalid or expired token.");
            return "redirect:/user/login";
        }
        
        model.addAttribute("token", token);
        return "resetpassword"; 
    }


    @PostMapping("/user/resetpassword")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model,
                                RedirectAttributes redi) { 
    	Optional<ResetPasswordToken> optionalToken = resetPasswordTokenRepository.findByToken(token);
    	if (!optionalToken.isPresent() || optionalToken.get().isExpired()) {
    	    redi.addFlashAttribute("error", "Invalid or expired token.");
    	    return "redirect:/login";
    	}
    	if (!newPassword.equals(confirmPassword)) {
            redi.addFlashAttribute("error", "Passwords do not match. Please try again.");
            return "redirect:/user/resetpassword?token=" + token;
        }
        
    	ResetPasswordToken resetPasswordToken = optionalToken.get();
        User user = resetPasswordToken.getUser();

        service.savePassword(user, confirmPassword);
        resetPasswordTokenRepository.delete(resetPasswordToken);

        redi.addFlashAttribute("message", "Your password has been successfully reset.");
        return "redirect:/user/login";
    }
}
