package org.hackncrypt.userservice.controllers;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.userservice.config.rabbitMQ.MQConfig;
import org.hackncrypt.userservice.exceptions.InvalidInputException;
import org.hackncrypt.userservice.model.dtos.UserAuthInfo;
import org.hackncrypt.userservice.model.dtos.auth.request.LoginRequest;
import org.hackncrypt.userservice.model.dtos.auth.request.RegisterRequest;
import org.hackncrypt.userservice.model.dtos.auth.response.LoginResponse;
import org.hackncrypt.userservice.model.dtos.auth.response.RegisterResponse;
import org.hackncrypt.userservice.service.jwt.JwtService;
import org.hackncrypt.userservice.service.user.UserService;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final RabbitTemplate rabbitTemplate;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService, UserDetailsService userDetailsService, RabbitTemplate rabbitTemplate) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/google/oauth/login")
    public ResponseEntity<LoginResponse> oauthLogin(@RequestBody String oauthToken){
        String[] chunks = oauthToken.split("\\.");
        String payload = new String(Base64.getDecoder().decode(chunks[1]));
        JSONObject payloadJson = new JSONObject(payload);
        String email = payloadJson.getString("email");
        String name = payloadJson.getString("name");
        String picture = payloadJson.getString("picture");
        if(!userService.existsByEmail(email)){
            userService.registerOauthUser(email,name,picture);
        }
        String username = userService.getUsernameFromUserEmail(email);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
        String token = jwtService.generateToken(authToken);
        return ResponseEntity.ok(
                LoginResponse.builder()
                        .message("Login successful!")
                        .accessToken(token)
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest userLoginRequestDto){
        String username = userLoginRequestDto.getUsername();
        String password = userLoginRequestDto.getPassword();
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        }
        catch (AuthenticationException e){
            log.error(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponse.builder()
                            .message("Invalid username or password!")
                            .build());
        }
        catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(LoginResponse.builder()
                            .message("Something went wrong while authenticating user")
                            .build());
        }
        String token;
        try {
            token = jwtService.generateToken(authentication);
        }
        catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(
                    LoginResponse.builder()
                            .message("Something went wrong while generating token")
                            .build()
            );
        }
        return ResponseEntity.ok(
                LoginResponse.builder()
                        .message("Authentication Successful!")
                        .accessToken(token)
                        .build()
        );
    }
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterRequest userRegisterDto){
        String token;
        try {
            if(Objects.isNull(userRegisterDto.getOtp())){
                userService.sendRegistrationEmailOtp(userRegisterDto);
                log.info("Otp send to email successfully!");
                return ResponseEntity.ok(
                        RegisterResponse.builder()
                                .message("Otp has sent to email successfully!")
                                .build()
                );
            }
            boolean otpValidated =
                    userService
                            .validateUserOtp(userRegisterDto.getEmail(),userRegisterDto.getOtp());
            if(!otpValidated){
                return ResponseEntity
                        .badRequest()
                        .body(RegisterResponse.builder()
                        .message("Invalid Otp!").build());
            }
            userService.registerUser(userRegisterDto);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userRegisterDto.getUsername());
            UsernamePasswordAuthenticationToken  authToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
            token = jwtService.generateToken(authToken);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        catch (InvalidInputException e){
            return ResponseEntity.badRequest().body(RegisterResponse.builder()
                            .message(e.getMessage()).build());
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().body(RegisterResponse.builder()
                    .message(e.getMessage()).build());
        }
        return ResponseEntity.ok(RegisterResponse
                .builder()
                .message("Registration Successful")
                .accessToken(token)
                .build());
    }
}
