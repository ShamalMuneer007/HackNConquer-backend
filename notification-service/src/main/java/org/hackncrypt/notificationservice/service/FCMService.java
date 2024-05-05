package org.hackncrypt.notificationservice.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class FCMService {

    @PostConstruct
    public void init() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .build();
            FirebaseApp.initializeApp(options);
        }
    }

    public void sendPushNotification(String token, String title, String body) throws FirebaseMessagingException {
        log.info("Setting up notification");
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
        log.info("Setting up message to token : {}",token);
        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();
        log.info("Sending message");
        FirebaseMessaging.getInstance().send(message);
    }
}
