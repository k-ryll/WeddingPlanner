package com.example.wedding.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import com.example.wedding.model.VerificationToken;
import com.example.wedding.repository.VerificationTokenRepository;
import com.example.wedding.repository.UserRepository;
import com.example.wedding.model.User;
import com.example.wedding.security.VerificationCodeGenerator; 
@Service
public class VerificationService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public String generateVerificationCode(User user) {
        String code = VerificationCodeGenerator.generateVerificationCode(); 
        VerificationToken verificationToken = new VerificationToken(code, user);
        verificationTokenRepository.save(verificationToken);
        return code;
    }

    public boolean verifyCode(String email, String code) {
        Optional<VerificationToken> optionalToken = verificationTokenRepository.findByToken(code);

        if (optionalToken.isEmpty()) {
            return false; 
        }

        VerificationToken verificationToken = optionalToken.get();

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
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
    
    @Scheduled(fixedRate = 3600000) // Runs every 1 hour
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
    }
}
