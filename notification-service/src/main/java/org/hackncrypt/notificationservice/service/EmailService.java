package org.hackncrypt.notificationservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final TemplateEngine templateEngine;
    private  final JavaMailSender javaMailSender;

    public EmailService(TemplateEngine templateEngine, JavaMailSender javaMailSender) {
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }
    public void sendOtpToMail(String email,String otp) throws MessagingException {
        if (otp != null) {
            Context context = new Context();
            context.setVariable("title", "Verify Your Email Address");
            context.setVariable("otp", otp);
            String body = templateEngine.process("confirmation", context);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Registration Verification");
            helper.setText(body, true);
            javaMailSender.send(message);
        }
    }
    }
