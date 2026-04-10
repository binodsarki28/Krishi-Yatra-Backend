package com.krishiYatra.krishiYatra.verification;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail = "onlycoding6969@gmail.com";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String toEmail, String otpCode) {
        String subject = "Krishi Yatra - Email Verification Code";
        String body = String.format(
                "Welcome to Krishi Yatra!\n\n" +
                "Your email verification code is: %s\n\n" +
                "This code will expire in 5 minutes.\n\n" +
                "If you didn't request this code, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Krishi Yatra Team",
                otpCode
        );
        sendEmail(toEmail, subject, body);
    }

    public void sendNotificationEmail(String toEmail, String subject, String body) {
        sendEmail(toEmail, "Krishi Yatra - " + subject, body);
    }

    private void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("EmailService: ERROR sending email to [" + toEmail + "]. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
