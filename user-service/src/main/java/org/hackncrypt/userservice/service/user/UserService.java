package org.hackncrypt.userservice.service.user;

import org.hackncrypt.userservice.model.dtos.UserAuthInfo;
import org.hackncrypt.userservice.model.dtos.auth.request.RegisterRequest;
import org.hackncrypt.userservice.model.entities.User;

public interface UserService {
    void registerUser(RegisterRequest userRegisterDto);

    void sendRegistrationEmailOtp(RegisterRequest email);

    boolean validateUserOtp(String email, Integer otp);

    boolean existsByEmail(String email);

     void registerOauthUser(String email, String name, String picture);

    UserAuthInfo getUserAuthInfoFromEmail(String email);

    String getUsernameFromUserEmail(String email);
}
