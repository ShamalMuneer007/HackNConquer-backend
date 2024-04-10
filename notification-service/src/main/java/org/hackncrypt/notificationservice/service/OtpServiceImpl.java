package org.hackncrypt.notificationservice.service;

import com.rabbitmq.client.Channel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.notificationservice.dto.OtpDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;

@Getter
@Service
@Slf4j
public class OtpServiceImpl implements OtpService {
    private final CountDownLatch latch = new CountDownLatch(1);
    private final RedisTemplate<String,String> redisTemplate;
    private final EmailService emailService;
    public OtpServiceImpl(RedisTemplate<String, String> redisTemplate, EmailService emailService) {
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
    }
    @Override
    public Boolean validateOtp(OtpDto otpDto) {
        try {
            log.info("otp entered by user : {}",otpDto.getOtp());
            log.info("fetching otp from redis cache....");
            String val = redisTemplate.opsForValue().get(otpDto.getEmail());
            log.info("Validating otp ....{}",val);
            if (val != null) {
                return val.equals(otpDto.getOtp());
            } else {
                log.warn("Otp Expired");
                return false;
            }
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException("Something went wrong!");
        }
    }
    @RabbitListener(queues = "message_queue", ackMode = "MANUAL")
    public void processOtp(String email, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            log.info("Message Received : {}", email);
            SecureRandom secureRandom = new SecureRandom();
            int otpNum = secureRandom.nextInt(9999);
            String otp = String.format("%04d", otpNum);
            redisTemplate.opsForValue().set(email,otp);
            emailService.sendOtpToMail(email,otp);
            log.info(redisTemplate.opsForValue().get(email));
            redisTemplate.expire(email, Duration.ofMinutes(5));
            channel.basicAck(tag, false);
            latch.countDown();
        }
        catch (Exception e){
            channel.basicNack(tag, false, true);
        }
    }
}
