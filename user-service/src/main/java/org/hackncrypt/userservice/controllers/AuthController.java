package org.hackncrypt.userservice.controllers;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.userservice.exceptions.InvalidInputException;
import org.hackncrypt.userservice.model.dtos.auth.request.LoginRequest;
import org.hackncrypt.userservice.model.dtos.auth.request.RegisterRequest;
import org.hackncrypt.userservice.model.dtos.auth.response.LoginResponse;
import org.hackncrypt.userservice.model.dtos.auth.response.RegisterResponse;
import org.hackncrypt.userservice.service.jwt.JwtService;
import org.hackncrypt.userservice.service.user.UserService;
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

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
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
                        .token(token)
                        .build()
        );
    }
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterRequest userRegisterDto){
        String token;
        try {
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
                .token(token)
                .build());
    }
}
