package com.example.wedding.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String recipientEmail, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("Your Email Verification Code");
            helper.setText("<p>Your verification code is:</p>"
                    + "<h2>" + verificationCode + "</h2>"
                    + "<p>Enter this code in the verification form to confirm your email.</p>"
                    +"http://localhost:8080/verify?email="+recipientEmail, true);

            mailSender.send(message);
            logger.info("Verification email sent successfully to {}", recipientEmail);

        } catch (MailException | MessagingException e) {
            logger.error("Failed to send verification email to {}: {}", recipientEmail, e.getMessage());
        }
    }
    
    public void sendResetPassword(String recipientEmail, String token) {
    	try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("Reset Your Password");
            helper.setText("Click here to reset your password: "
                    + "http://localhost:8080/user/resetpassword?token=" + token);

            mailSender.send(message);
            logger.info("Verification email sent successfully to {}", recipientEmail);

        } catch (MailException | MessagingException e) {
            logger.error("Failed to send verification email to {}: {}", recipientEmail, e.getMessage());
        }
    }
}
