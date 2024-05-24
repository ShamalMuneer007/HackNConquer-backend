package org.hackncrypt.userservice.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hackncrypt.userservice.model.entities.User;
import org.hackncrypt.userservice.model.entities.UserAudit;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAuditDto {
    private User user;
    private Long auditId;
    private String ipAddr;
    private LocalDateTime loginTime;
    public UserAuditDto(UserAudit userAudit){
        this.auditId = userAudit.getAuditId();
        this.ipAddr = userAudit.getUserIpAddress();
        this.loginTime = userAudit.getLoginTime();
        this.user = userAudit.getUser();
    }
}
