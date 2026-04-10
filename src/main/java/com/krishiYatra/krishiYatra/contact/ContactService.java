package com.krishiYatra.krishiYatra.contact;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final JavaMailSender mailSender;

    public ServerResponse sendContactEmail(ContactRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(request.getEmail());
            message.setTo("onlycoding6969@gmail.com");
            message.setSubject("KrishiYatra Contact: " + request.getSubject());
            message.setText("Name: " + request.getName() + "\n" +
                           "Email: " + request.getEmail() + "\n\n" +
                           "Message:\n" + request.getMessage());
            
            mailSender.send(message);
            log.info("Contact email sent from {}", request.getEmail());
            return ServerResponse.successResponse("Thank you for contacting us. We have received your message.", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to send contact email from {}", request.getEmail(), e);
            return ServerResponse.failureResponse("Could not send email. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
