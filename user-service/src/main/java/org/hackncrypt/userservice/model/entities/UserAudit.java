package org.hackncrypt.userservice.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hackncrypt.userservice.enums.UserLogStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long auditId;
    @ManyToOne
    private User user;
    @Column(nullable = false)
    private String userIpAddress;
    @Column(nullable = false)
    private LocalDateTime loginTime;
    @Enumerated(EnumType.STRING)
    private UserLogStatus userLogStatus;
    private boolean inactiveMailSend = false;
    private LocalDateTime logoutTime;
}
