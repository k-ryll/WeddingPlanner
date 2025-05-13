package com.example.wedding.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.stream.Collectors;
import com.example.wedding.model.Guest;
import com.example.wedding.model.Task;
import com.example.wedding.model.Project;
import com.example.wedding.model.ItineraryItem;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${app.base-url}")
    private String appBaseUrl;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private GuestService guestService;

    public void sendVerificationEmail(String recipientEmail, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("Your Email Verification Code");
            String verificationLink = appBaseUrl + "/verify?email=" + recipientEmail;
            helper.setText("<p>Your verification code is:</p>"
                    + "<h2>" + verificationCode + "</h2>"
                    + "<p>Enter this code in the verification form to confirm your email.</p>"
                    + "<p><a href=\"" + verificationLink + "\">Click here to verify</a></p>", true);

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
            String resetLink = appBaseUrl + "/user/resetpassword?token=" + token;
            helper.setText("Click here to reset your password: "
                    + "<a href=\"" + resetLink + "\">" + resetLink + "</a>", true);

            mailSender.send(message);
            logger.info("Reset password email sent successfully to {}", recipientEmail);

        } catch (MailException | MessagingException e) {
            logger.error("Failed to send reset password email to {}: {}", recipientEmail, e.getMessage());
        }
    }

    public void sendInvitationEmail(String to, String guestName, String projectName) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo(to);
            helper.setSubject("Wedding Invitation - " + projectName);
            
            String acceptLink = String.format("%s/guest/rsvp?email=%s&response=accept", appBaseUrl, to);
            String declineLink = String.format("%s/guest/rsvp?email=%s&response=decline", appBaseUrl, to);

            String htmlContent = String.format("""
                <html>
                <body style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>
                    <h2 style='color: #814256; text-align: center;'>Wedding Invitation</h2>
                    <p>Dear %s,</p>
                    <p>You have been invited to the wedding of %s. </p>
                    <p>Please click one of the following buttons to respond to the invitation:</p>
                    <div style='text-align: center; margin: 30px 0;'>
                        <a href='%s' 
                           style='background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-right: 10px;'>
                            Accept
                        </a>
                        <a href='%s' 
                           style='background-color: #f44336; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>
                            Decline
                        </a>
                    </div>
                    <p style='color: #666; font-size: 14px;'>If you have any questions, please don\'t hesitate to contact us.</p>
                </body>
                </html>
            """, guestName, projectName, acceptLink, declineLink);
            
            helper.setText(htmlContent, true);
            
            logger.info("Attempting to send invitation email to: {}", to);
            mailSender.send(message);
            logger.info("Successfully sent invitation email to: {}", to);
            
        } catch (MailException e) {
            logger.error("MailException while sending invitation to {}: {}", to, e.getMessage(), e);
            throw new MessagingException("Failed to send email: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending invitation to {}: {}", to, e.getMessage(), e);
            throw new MessagingException("Unexpected error while sending email: " + e.getMessage(), e);
        }
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

    private void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    public void sendTaskToEntourage(Project project, String entourageType, String taskName, String taskDescription, String dueDate) {
        List<Guest> entourageMembers;
        
        if ("ALL".equals(entourageType)) {
            entourageMembers = guestService.findByProject(project).stream()
                .filter(guest -> !"Guest".equals(guest.getEntourage()))
                .collect(Collectors.toList());
        } else if ("Father of the Bride".equals(entourageType)) {
            // Get both parents of the bride
            entourageMembers = guestService.findByProject(project).stream()
                .filter(guest -> "Father of the Bride".equals(guest.getEntourage()) || 
                               "Mother of the Bride".equals(guest.getEntourage()))
                .collect(Collectors.toList());
        } else if ("Father of the Groom".equals(entourageType)) {
            // Get both parents of the groom
            entourageMembers = guestService.findByProject(project).stream()
                .filter(guest -> "Father of the Groom".equals(guest.getEntourage()) || 
                               "Mother of the Groom".equals(guest.getEntourage()))
                .collect(Collectors.toList());
        } else {
            entourageMembers = guestService.findByProject(project).stream()
                .filter(guest -> entourageType.equals(guest.getEntourage()))
                .collect(Collectors.toList());
        }
        
        if (entourageMembers.isEmpty()) {
            logger.warn("No entourage members found for type: {}", entourageType);
            return;
        }

        String subject = "Wedding Task Assignment: " + taskName;
        String content = String.format(
            "Task: %s\n\nDescription: %s\nDue Date: %s\n\nPlease complete this task by the due date.",
            taskName, taskDescription, dueDate
        );

        for (Guest member : entourageMembers) {
            try {
                sendEmail(member.getEmail(), subject, content);
                logger.info("Task email sent to {} ({})", member.getName(), member.getEmail());
            } catch (Exception e) {
                logger.error("Failed to send task email to {}: {}", member.getEmail(), e.getMessage());
            }
        }
    }

    public void sendTaskToAllEntourage(Project project, String taskName, String taskDescription, String dueDate) {
        // Get all guests except those marked as "Guest"
        List<Guest> allEntourage = guestService.findByProject(project).stream()
            .filter(guest -> !"Guest".equals(guest.getEntourage()))
            .collect(Collectors.toList());
        
        if (allEntourage.isEmpty()) {
            logger.warn("No entourage members found in the guest list");
            return;
        }

        String subject = "Wedding Task Assignment: " + taskName;
        String content = String.format(
            "Task: %s\n\nDescription: %s\nDue Date: %s\n\nPlease complete this task by the due date.",
            taskName, taskDescription, dueDate
        );

        for (Guest member : allEntourage) {
            try {
                sendEmail(member.getEmail(), subject, content);
                logger.info("Task email sent to {} ({})", member.getName(), member.getEmail());
            } catch (Exception e) {
                logger.error("Failed to send task email to {}: {}", member.getEmail(), e.getMessage());
            }
        }
    }

    public void sendItineraryToEntourage(Project project, String entourageType, List<ItineraryItem> itinerary) {
        List<Guest> entourageMembers;
        
        if ("Father of the Bride".equals(entourageType)) {
            // Get both parents of the bride
            entourageMembers = guestService.findByProject(project).stream()
                .filter(guest -> "Father of the Bride".equals(guest.getEntourage()) || 
                               "Mother of the Bride".equals(guest.getEntourage()))
                .collect(Collectors.toList());
        } else if ("Father of the Groom".equals(entourageType)) {
            // Get both parents of the groom
            entourageMembers = guestService.findByProject(project).stream()
                .filter(guest -> "Father of the Groom".equals(guest.getEntourage()) || 
                               "Mother of the Groom".equals(guest.getEntourage()))
                .collect(Collectors.toList());
        } else {
            entourageMembers = guestService.findByProject(project).stream()
                .filter(guest -> entourageType.equals(guest.getEntourage()))
                .collect(Collectors.toList());
        }
        
        if (entourageMembers.isEmpty()) {
            logger.warn("No entourage members found for type: {}", entourageType);
            return;
        }

        String subject = "Wedding Day Itinerary";
        StringBuilder content = new StringBuilder();
        content.append("Wedding Day Schedule:\n\n");
        
        for (ItineraryItem item : itinerary) {
            content.append(String.format("%s - %s: %s\n", 
                item.getStartTime(), 
                item.getEndTime(), 
                item.getTitle()));
            if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                content.append("Description: ").append(item.getDescription()).append("\n");
            }
            if (item.getLocation() != null && !item.getLocation().isEmpty()) {
                content.append("Location: ").append(item.getLocation()).append("\n");
            }
            content.append("\n");
        }

        for (Guest member : entourageMembers) {
            try {
                sendEmail(member.getEmail(), subject, content.toString());
                logger.info("Itinerary email sent to {} ({})", member.getName(), member.getEmail());
            } catch (Exception e) {
                logger.error("Failed to send itinerary email to {}: {}", member.getEmail(), e.getMessage());
            }
        }
    }

    public void sendItineraryToAllEntourage(Project project, List<ItineraryItem> itinerary) {
        // Get all guests except those marked as "Guest"
        List<Guest> allEntourage = guestService.findByProject(project).stream()
            .filter(guest -> !"Guest".equals(guest.getEntourage()))
            .collect(Collectors.toList());
        
        if (allEntourage.isEmpty()) {
            logger.warn("No entourage members found in the guest list for itinerary");
            return;
        }

        String subject = "Wedding Day Itinerary";
        StringBuilder content = new StringBuilder();
        content.append("Wedding Day Schedule:\n\n");
        
        for (ItineraryItem item : itinerary) {
            content.append(String.format("%s - %s: %s\n", 
                item.getStartTime(), 
                item.getEndTime(), 
                item.getTitle()));
            if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                content.append("Description: ").append(item.getDescription()).append("\n");
            }
            if (item.getLocation() != null && !item.getLocation().isEmpty()) {
                content.append("Location: ").append(item.getLocation()).append("\n");
            }
            content.append("\n");
        }

        for (Guest member : allEntourage) {
            try {
                sendEmail(member.getEmail(), subject, content.toString());
                logger.info("Itinerary email sent to {} ({})", member.getName(), member.getEmail());
            } catch (Exception e) {
                logger.error("Failed to send itinerary email to {}: {}", member.getEmail(), e.getMessage());
            }
        }
    }
}
