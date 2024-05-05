package org.hackncrypt.notificationservice.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.InputStream;

//@Configuration
//@Slf4j
//public class FirebaseConfiguration {
//    @PostConstruct
//    public void firebaseInit() throws IOException {
//        try {
//            ClassPathResource resource = new ClassPathResource("firebase.json");
//
//            FirebaseOptions options = new FirebaseOptions
//                    .Builder()
//                    .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
//                    .build();
//            FirebaseApp.initializeApp(options);
//            isFirebaseActive = true;
//        } catch (Exception e) {
//            logger.error("Post Construct: {}", e);
//        }
//    }
//}
