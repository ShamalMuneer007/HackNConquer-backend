package org.hackncrypt.notificationservice.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
@Slf4j
public class FCMInitializer {

    @Value("${firebase-configuration-file}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() throws IOException {
        try {
            GoogleCredentials credentials;
            if (new File(firebaseConfigPath).exists()) {
                credentials = GoogleCredentials.fromStream(new FileInputStream(firebaseConfigPath));
            } else {
                credentials = GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream());
            }
            FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials).build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application initialized");
            }
        } catch (IOException e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage());
            throw e;
        }
    }
}