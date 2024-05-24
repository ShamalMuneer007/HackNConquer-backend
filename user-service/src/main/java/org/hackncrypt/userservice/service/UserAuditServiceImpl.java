package org.hackncrypt.userservice.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.userservice.config.rabbitMQ.MQConfig;
import org.hackncrypt.userservice.controllers.UserAuditDto;
import org.hackncrypt.userservice.enums.UserLogStatus;
import org.hackncrypt.userservice.model.entities.User;
import org.hackncrypt.userservice.model.entities.UserAudit;
import org.hackncrypt.userservice.repositories.UserAuditRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuditServiceImpl implements UserAuditService {
    private final UserAuditRepository userAuditRepository;
    private final RabbitTemplate rabbitTemplate;
    @Override
    public void logUserLogin(User user, HttpServletRequest request) {
            UserAudit userAudit = UserAudit.builder()
                    .userIpAddress(request.getRemoteAddr())
                    .user(user)
                    .loginTime(LocalDateTime.now())
                    .userLogStatus(UserLogStatus.LOGGED_IN)
                    .build();
            userAuditRepository.save(userAudit);
    }

    @Override
    public List<UserAuditDto> getUserAuditLogs() {
        return userAuditRepository.findAll().stream().map(UserAuditDto::new).collect(Collectors.toList());
    }

    @Override
    public void logoutUser(Long userId) {
        log.info("Logging out user");
        List<UserAudit> userAudits = userAuditRepository.findByUserUserId(userId);
        List<UserAudit> userAuditList = userAudits.stream()
                .sorted((a, b) -> b.getLoginTime().compareTo(a.getLoginTime()))
                .toList();
        UserAudit userAudit = userAuditList.get(0);
        userAudit.setUserLogStatus(UserLogStatus.LOGGED_OUT);
        userAudit.setLogoutTime(LocalDateTime.now());
        userAuditRepository.save(userAudit);
    }
    @Scheduled(cron = "0 0 0 * * *")
    public void runDailyTask() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<UserAudit> userAudits = userAuditRepository.findLatestLoggedOutUsersBefore5Days(sevenDaysAgo);
        List<UserAudit> list = userAudits.stream().map(userAudit -> {
            if(userAudit.isInactiveMailSend()){
                return userAudit;
            }
            rabbitTemplate.convertAndSend(MQConfig.INACTIVE_EMAIL_EXCHANGE,
                    MQConfig.INACTIVE_EMAIL_ROUTINGKEY, userAudit.getUser().getEmail());
            userAudit.setInactiveMailSend(true);
            return userAudit;
        }
        ).toList();
        userAuditRepository.saveAll(list);

    }
}
