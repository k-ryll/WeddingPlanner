package com.example.wedding.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import com.example.wedding.model.VerificationToken;
import com.example.wedding.repository.VerificationTokenRepository;
import com.example.wedding.repository.ResetPasswordTokenRepository;
import com.example.wedding.repository.UserRepository;
import com.example.wedding.config.VerificationCodeGenerator;
import com.example.wedding.model.ResetPasswordToken;
import com.example.wedding.model.User; 
@Service
public class VerificationService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    
    @Autowired
    private ResetPasswordTokenRepository resetPasswordTokenRepository;
    @Autowired
    private UserRepository userRepository;

    public String generateVerificationCode(User user) {
        String code = VerificationCodeGenerator.generateVerificationCode(); 
        VerificationToken verificationToken = new VerificationToken(code, user);
        verificationTokenRepository.save(verificationToken);
        return code;
    }
    
    public String generateResetPasswordToken(User user) {
        String token = VerificationCodeGenerator.generatePasswordToken(); 
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken(token, user);
        resetPasswordTokenRepository.save(resetPasswordToken);
        return token;
    }

    public boolean verifyCode(String email, String code) {
        Optional<VerificationToken> optionalToken = verificationTokenRepository.findByToken(code);

        
        if (optionalToken.isEmpty()) {
            return false; 
        }

        VerificationToken verificationToken = optionalToken.get();

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
        	 User user = verificationToken.getUser();
        	 
        	verificationTokenRepository.delete(verificationToken);
            userRepository.delete(user);
            System.out.println("Deleted unverified user, token expired: " + user.getEmail());
            return false; 
        }

        User user = verificationToken.getUser();

        if (!user.getEmail().equals(email)) {
            return false; 
        }

   
        user.setVerified(true);
        userRepository.save(user); 

    
        verificationTokenRepository.delete(verificationToken);

        return true; 
    }
    
    @Scheduled(fixedRate = 3600000) 
    public void removeExpiredUsers() {
        List<VerificationToken> expiredTokens = verificationTokenRepository.findByExpiryDateBefore(LocalDateTime.now());
        
        for (VerificationToken token : expiredTokens) {
            User user = token.getUser();

            if (!user.isVerified()) { 
                verificationTokenRepository.delete(token);
                userRepository.delete(user);
                System.out.println("Deleted unverified user: " + user.getEmail());
            }
        }
        List<ResetPasswordToken> expiredResetTokens =
                resetPasswordTokenRepository.findByExpiryDateBefore(LocalDateTime.now());
        
        for (ResetPasswordToken resetToken : expiredResetTokens) {
            resetPasswordTokenRepository.delete(resetToken);
            System.out.println("Deleted expired reset password token for user: " + resetToken.getUser().getEmail());
        }
    }
}
