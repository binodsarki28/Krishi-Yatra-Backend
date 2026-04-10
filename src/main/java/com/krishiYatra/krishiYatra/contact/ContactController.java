package com.krishiYatra.krishiYatra.contact;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/send")
    public ResponseEntity<ServerResponse> sendContactMessage(@Valid @RequestBody ContactRequest request) {
        ServerResponse response = contactService.sendContactEmail(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
