package org.hackncrypt.userservice.service;

import jakarta.servlet.http.HttpServletRequest;
import org.hackncrypt.userservice.controllers.UserAuditDto;
import org.hackncrypt.userservice.model.entities.User;

import java.util.List;

public interface UserAuditService {
    void logUserLogin(User user, HttpServletRequest request);

    List<UserAuditDto> getUserAuditLogs();

    void logoutUser(Long userId);
}
