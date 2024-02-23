package org.hackncrypt.userservice.service.user;

import org.hackncrypt.userservice.model.dtos.auth.request.RegisterRequest;

public interface UserService {
    void registerUser(RegisterRequest userRegisterDto);
}
