package org.hackncrypt.notificationservice.service;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.notificationservice.dto.CommentNotificationDto;
import org.hackncrypt.notificationservice.integrations.UserFeignProxy;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@RequiredArgsConstructor
@Slf4j
@Service
public class PushNotificationServiceImpl implements PushNotificationService{
    private final CountDownLatch latch = new CountDownLatch(1);
    private final UserFeignProxy userFeignProxy;
    private final FCMService fcmService;
    @RabbitListener(queues = "web_fmc_queue", ackMode = "MANUAL")
    public void sendCommentPushNotification(CommentNotificationDto commentNotificationDto, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            log.info("Comment Notification Message Received : {}", commentNotificationDto.getToUserId());
            String userDeviceToken =
                    Objects.requireNonNull(userFeignProxy.getUserDeviceToken(Long.parseLong(commentNotificationDto.getToUserId())).getBody()).getUserDeviceToken();
            String title = commentNotificationDto.getToUsername() + ", Someone has commented to your discussion";
            String body = commentNotificationDto.getCommentedUsername() +
                    " has commented at : " +
                    commentNotificationDto.getCommentedAt() + " \n "+
                    "\n\n  Comment : \n\n"+commentNotificationDto.getComment();
            fcmService.sendPushNotification(userDeviceToken,title,body);
            channel.basicAck(tag, false);
            latch.countDown();
        }
        catch(NullPointerException e){
            log.error("Null Pointer Exception : {}",e.getMessage());
        }
        catch (Exception e){
            log.error("Exception occured : {}",e.getMessage());
            channel.basicNack(tag, false, true);
        }
    }
}
