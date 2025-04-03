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
import java.util.List;
import com.example.wedding.model.Guest;

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

    public void sendInvitationEmail(String to, String guestName, String projectName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setTo(to);
        helper.setSubject("Wedding Invitation - " + projectName);
        
        String htmlContent = String.format("""
            <html>
            <body style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>
                <h2 style='color: #814256; text-align: center;'>Wedding Invitation</h2>
                <p>Dear %s,</p>
                <p>You have been invited to the wedding of %s. </p>
                <p>Please click one of the following buttons to respond to the invitation:</p>
                <div style='text-align: center; margin: 30px 0;'>
                    <a href='http://localhost:8080/guest/rsvp?email=%s&response=accept' 
                       style='background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-right: 10px;'>
                        Accept
                    </a>
                    <a href='http://localhost:8080/guest/rsvp?email=%s&response=decline' 
                       style='background-color: #f44336; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>
                        Decline
                    </a>
                </div>
                <p style='color: #666; font-size: 14px;'>If you have any questions, please don't hesitate to contact us.</p>
            </body>
            </html>
        """, guestName, projectName, to, to);
        
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    public void sendBulkInvitations(List<Guest> guests, String projectName) {
        for (Guest guest : guests) {
            try {
                sendInvitationEmail(guest.getEmail(), guest.getName(), projectName);
                logger.info("Invitation sent successfully to {}", guest.getEmail());
            } catch (MessagingException e) {
                logger.error("Failed to send invitation to {}: {}", guest.getEmail(), e.getMessage());
            }
        }
    }
}
