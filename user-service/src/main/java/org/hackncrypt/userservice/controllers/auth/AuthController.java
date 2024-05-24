package org.hackncrypt.userservice.controllers.auth;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.userservice.model.dto.LogoutResponse;
import org.hackncrypt.userservice.model.dto.auth.request.LoginRequest;
import org.hackncrypt.userservice.model.dto.auth.request.RegisterRequest;
import org.hackncrypt.userservice.model.dto.auth.response.LoginResponse;
import org.hackncrypt.userservice.model.dto.auth.response.RegisterResponse;
import org.hackncrypt.userservice.model.entities.User;
import org.hackncrypt.userservice.service.UserAuditService;
import org.hackncrypt.userservice.service.jwt.JwtService;
import org.hackncrypt.userservice.service.user.UserService;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final UserAuditService userAuditService;

    @PostMapping("/google/oauth/login")
    public ResponseEntity<LoginResponse> oauthLogin(@RequestBody String oauthToken, HttpServletRequest request){
        String[] chunks = oauthToken.split("\\.");
        String payload = new String(Base64.getDecoder().decode(chunks[1]));
        JSONObject payloadJson = new JSONObject(payload);
        String email = payloadJson.getString("email");
        String name = payloadJson.getString("name");
        String picture = payloadJson.getString("picture");
        if(!userService.existsByEmail(email)){
            userService.registerOauthUser(email,name,picture);
        }
        User user = userService.getUsernameFromUserEmail(email);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
        String token = jwtService.generateToken(authToken);
        log.info("T");
        userAuditService.logUserLogin(user,request);
        return ResponseEntity.ok(
                LoginResponse.builder()
                        .message("Login successful!")
                        .accessToken(token)
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest userLoginRequestDto,HttpServletRequest request){
            String token = userService.authenticateUser(userLoginRequestDto,request);
            return ResponseEntity.ok(
                    LoginResponse.builder()
                            .message("Authentication Successful!")
                            .accessToken(token)
                            .build());
    }
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterRequest userRegisterDto,HttpServletRequest request){
            if(Objects.isNull(userRegisterDto.getOtp())){
                userService.sendRegistrationEmailOtp(userRegisterDto);
                log.info("Otp send to email successfully!");
                return ResponseEntity.ok(
                        RegisterResponse.builder()
                                .message("Otp has sent to email successfully!")
                                .build()
                );
            }
            log.info("USER REGISTER OTP : {}",userRegisterDto.getOtp());
            boolean otpValidated =
                    userService
                            .validateUserOtp(userRegisterDto.getEmail(),userRegisterDto.getOtp());
            if(!otpValidated){
                return ResponseEntity
                        .badRequest()
                        .body(RegisterResponse.builder()
                        .message("Invalid Otp!").build());
            }
            String token = userService.registerUser(userRegisterDto,request);
        return ResponseEntity.ok(RegisterResponse
                .builder()
                .message("Registration Successful")
                .accessToken(token)
                .build());
    }
    @PostMapping("/logout/{userId}")
    public ResponseEntity<LogoutResponse> logoutUser(@PathVariable Long userId){
        userAuditService.logoutUser(userId);
        return ResponseEntity.ok(new LogoutResponse("User logged out successfully", HttpStatus.OK.value()));
    }

}
