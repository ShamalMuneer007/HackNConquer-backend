package org.hackncrypt.userservice.service.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.userservice.enums.Role;
import org.hackncrypt.userservice.exceptions.InvalidInputException;
import org.hackncrypt.userservice.exceptions.NoSuchValueException;
import org.hackncrypt.userservice.exceptions.UserAuthenticationException;
import org.hackncrypt.userservice.exceptions.business.UserNotFoundException;
import org.hackncrypt.userservice.integrations.notificationservice.NotificationFeignProxy;
import org.hackncrypt.userservice.model.dto.UserDto;
import org.hackncrypt.userservice.model.dto.auth.UserAuthInfo;
import org.hackncrypt.userservice.model.dto.auth.OtpDto;
import org.hackncrypt.userservice.model.dto.auth.request.LoginRequest;
import org.hackncrypt.userservice.model.dto.auth.request.RegisterRequest;
import org.hackncrypt.userservice.model.dto.request.    IncreaseXpRequest;
import org.hackncrypt.userservice.model.entities.User;
import org.hackncrypt.userservice.repositories.UserRepository;
import org.hackncrypt.userservice.service.jwt.JwtService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.hackncrypt.userservice.config.rabbitMQ.MQConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final NotificationFeignProxy notificationFeignProxy;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    public String registerUser(RegisterRequest userRegisterDto) {

            validateUserRegisterDto(userRegisterDto);
            User user = User.builder()
                    .username(userRegisterDto.getUsername())
                    .email(userRegisterDto.getEmail())
                    .password(passwordEncoder.encode(userRegisterDto.getPassword()))
                    .isBlocked(false)
                    .isPremium(false)
                    .level(1)
                    .role(userRepository.count() < 1 ? Role.ROLE_ADMIN : Role.ROLE_USER)
                    .xp(0)
                    .created_at(new Date())
                    .build();
            user = userRepository.save(user);
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            String token = jwtService.generateToken(authToken);
            SecurityContextHolder.getContext().setAuthentication(authToken);
            return token;
    }

    @Override
    public void sendRegistrationEmailOtp(RegisterRequest registerDto) {
        validateUserRegisterDto(registerDto);
        try{
            log.info("Sending message to notification service for otp of the user..");
            rabbitTemplate.convertAndSend(MQConfig.EXCHANGE,
                    MQConfig.ROUTING_KEY, registerDto.getEmail());
        }
        catch (Exception e){
            log.error("Something went wrong while sending message to queue");
            throw new AmqpException("Something went wrong while sending message to queue");
        }
    }

    @Override
    public boolean validateUserOtp(String email, String otp) {
        OtpDto otpDto = new OtpDto(email,otp);
        try {
            Boolean response = false;
            log.info("USER ENTERED OTP : {}",otp);
            response = notificationFeignProxy.validateOtp(otpDto).getBody();
            return response;
        }
        catch (Exception e){
         log.error("ERROR : {}",e.getMessage());
         throw new AmqpException("Something went wrong while getting response back");
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void registerOauthUser(String email, String name, String picture) {
        User user = User.builder()
                .created_at(new Date())
                .isPremium(false)
                .email(email)
                .username(name.replace(" ",""))
                .isBlocked(false)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .role(Role.ROLE_USER)
                .level(1)
                .xp(0)
                .profileImageUrl(picture).build();
        userRepository.save(user);
    }

    @Override
    public UserAuthInfo getUserAuthInfoFromEmail(String email) {
       User user = userRepository.findByEmail(email);
       return new UserAuthInfo(user.getUsername(),user.getRole());
    }

    @Override
    public String getUsernameFromUserEmail(String email) {
        return userRepository.findByEmail(email).getUsername();
    }

    @Override
    public String authenticateUser(LoginRequest LoginRequest) {
        String username = LoginRequest.getUsername();
        String password = LoginRequest.getPassword();
        try {
           Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
           return jwtService.generateToken(authentication);
        }
        catch (AuthenticationException e){
            log.error(e.getMessage());
            throw new UserAuthenticationException(e.getMessage());
        }
        catch (Exception e){
            throw e;
        }
    }

    @Override
    public Page<UserDto> getAllUsers(int page, int size) {
            Pageable pageable = PageRequest.of(page-1, size);
            Page<User> userPage = userRepository.findAllByIsDeletedIsFalse(pageable);
            return userPage.map(UserDto::new);
    }

    @Override
    public void deleteUserByUserId(Long userId) {
        if(userRepository.existsById(userId)){
            throw new NoSuchValueException("User does not exists with this id");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public void increaseUserXp(IncreaseXpRequest increaseXpRequest) {
        Optional<User> userOptional = userRepository.findById(increaseXpRequest.getUserId());
        Supplier<? extends RuntimeException> noUserException =
                () -> new UserNotFoundException("User not found with ID: " + increaseXpRequest.getUserId());
        User user = userOptional
                .orElseThrow(noUserException);
        int newXp = user.getXp() + increaseXpRequest.getXp();
        while (newXp >= user.getCurrentMaxXp()) {
            int prevMaxXp = user.getCurrentMaxXp();
            levelUp(user);
            newXp = Math.abs(newXp - prevMaxXp);
        }
        user.setXp(newXp);
        userRepository.save(user);
    }

    private void levelUp(User user) {
        user.setLevel(user.getLevel() + 1);
        user.setCurrentMaxXp(user.getLevel() * 50);
    }

    @Override
    public UserDto getUserData(HttpServletRequest request) {
        Long userId = Long.valueOf(jwtService.getUserIdFromRequest(request));
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() ->  new UserNotFoundException("No user with userId "+userId));
        return new UserDto(user);
    }

    //Validate User Inputs
    private void validateUserRegisterDto(RegisterRequest registerRequest){
        if(registerRequest == null) {
            log.warn("User identity cannot be null");
            throw new InvalidInputException("User identity cannot be null");
        }
        if(userRepository.existsByUsername(registerRequest.getUsername())){
            log.warn("Username already exists!");
            throw new InvalidInputException("Username already exists!");
        }
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            log.warn("Email already exists!");
            throw new InvalidInputException("Email already exists!");
        }
        if(registerRequest.getUsername().isEmpty()) {
            log.warn("Username cannot be empty");
            throw new InvalidInputException("Username cannot be empty");
        }
        if(registerRequest.getEmail().isEmpty()) {
            log.warn("Email cannot be empty");
            throw new InvalidInputException("Email cannot be empty");
        }
        if(registerRequest.getPassword().isEmpty()) {
            log.warn("Password cannot be empty");
            throw new InvalidInputException("Password cannot be empty");
        }

        if(!isValidPassword(registerRequest.getPassword())) {
            log.warn("Invalid password");
            throw new InvalidInputException("Invalid password");
        }
    }
    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,128}$"; // 8-128 characters, one lowercase, one uppercase, one digit, one special character
        if (!password.matches(regex)) {
            log.warn("Password must meet complexity requirements: 8-128 characters, 1 lowercase, 1 uppercase, 1 digit, 1 special character.");
            throw new InvalidInputException("Password must meet complexity requirements: 8-128 characters, 1 lowercase, 1 uppercase, 1 digit, 1 special character.");
        }
        if (password.length() < 8) {
            log.warn("Password must be at least 8 characters long.");
            throw new InvalidInputException("Password must be at least 8 characters long.");
        }
        if (password.length() > 128) {
            log.warn("Password cannot exceed 128 characters.");
            throw new InvalidInputException("Password cannot exceed 128 characters.");
        }
        return true;
    }
}
