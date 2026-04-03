package com.krishiYatra.krishiYatra.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("firebase/krishiyatra-2b50a-firebase-adminsdk-fbsvc-dfb1d304d7.json");

            if (!resource.exists()) {
                System.err.println("Firebase configuration file not found in classpath!");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase App has been initialized.");
            }
        } catch (IOException e) {
            System.err.println("Error initializing Firebase App: " + e.getMessage());
        }
    }
}
