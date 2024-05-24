package org.hackncrypt.notificationservice.service;

import com.rabbitmq.client.Channel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

@Service
@Slf4j
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
    @RabbitListener(queues = "inactive_email_queue", ackMode = "MANUAL")
    public void SendEmailToInactiveUser(String email,Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException, MessagingException {
        try {
            log.info("Sending email to : {}",email);
            if(email == null || email.isEmpty()){
                return;
            }
            Context context = new Context();
            context.setVariable("title", "NEW PROBLEMS ARE WAITING FOR YOU");
            String body = templateEngine.process("inactive", context);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("INACTIVE EMAIL");
            helper.setText(body, true);
            javaMailSender.send(message);
            channel.basicAck(tag, false);
        }
          catch (Exception e){
            log.error("Exception occured : {}",e.getMessage());
            channel.basicNack(tag, false, true);
        }
    }
    }
