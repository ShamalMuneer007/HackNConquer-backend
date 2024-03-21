package org.hackncrypt.userservice.service.user;

import org.hackncrypt.userservice.model.dto.UserDto;
import org.hackncrypt.userservice.model.dto.auth.UserAuthInfo;
import org.hackncrypt.userservice.model.dto.auth.request.LoginRequest;
import org.hackncrypt.userservice.model.dto.auth.request.RegisterRequest;
import org.springframework.data.domain.Page;

public interface UserService {
    String registerUser(RegisterRequest userRegisterDto);

    void sendRegistrationEmailOtp(RegisterRequest email);

    boolean validateUserOtp(String email, Integer otp);

    boolean existsByEmail(String email);

     void registerOauthUser(String email, String name, String picture);

    UserAuthInfo getUserAuthInfoFromEmail(String email);

    String getUsernameFromUserEmail(String email);

    String authenticateUser(LoginRequest userLoginRequestDto);

    Page<UserDto> getAllUsers(int page, int size);
}
