package org.hackncrypt.userservice.service.user;

import com.rabbitmq.client.Channel;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.userservice.exceptions.business.UserClanException;
import org.hackncrypt.userservice.model.dto.request.*;
import org.hackncrypt.userservice.model.dto.response.*;
import org.hackncrypt.userservice.enums.Role;
import org.hackncrypt.userservice.exceptions.InvalidInputException;
import org.hackncrypt.userservice.exceptions.NoSuchValueException;
import org.hackncrypt.userservice.exceptions.UserAuthenticationException;
import org.hackncrypt.userservice.exceptions.business.FriendRequestException;
import org.hackncrypt.userservice.exceptions.business.UserNotFoundException;
import org.hackncrypt.userservice.integrations.notificationservice.NotificationFeignProxy;
import org.hackncrypt.userservice.model.dto.LeaderboardDto;
import org.hackncrypt.userservice.model.dto.UserDto;
import org.hackncrypt.userservice.model.dto.auth.UserAuthInfo;
import org.hackncrypt.userservice.model.dto.auth.OtpDto;
import org.hackncrypt.userservice.model.dto.auth.request.LoginRequest;
import org.hackncrypt.userservice.model.dto.auth.request.RegisterRequest;
import org.hackncrypt.userservice.model.entities.Friend;
import org.hackncrypt.userservice.model.entities.FriendRequest;
import org.hackncrypt.userservice.model.entities.User;

import org.hackncrypt.userservice.repositories.FriendRepository;
import org.hackncrypt.userservice.repositories.FriendRequestRepository;
import org.hackncrypt.userservice.repositories.UserRepository;
import org.hackncrypt.userservice.service.UserAuditService;
import org.hackncrypt.userservice.service.jwt.JwtService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.hackncrypt.userservice.config.rabbitMQ.MQConfig;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.Header;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
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
    private final FriendRequestRepository friendRequestRepository;
    private final FriendRepository friendRepository;
    private final UserAuditService userAuditService;

    @Override
    @Transactional
    public String registerUser(RegisterRequest userRegisterDto, HttpServletRequest request) {

            validateUserRegisterDto(userRegisterDto);
            User user = User.builder()
                    .username(userRegisterDto.getUsername())
                    .email(userRegisterDto.getEmail())
                    .password(passwordEncoder.encode(userRegisterDto.getPassword()))
                    .isBlocked(false)
                    .isPremium(false)
                    .level(1)
                    .currentMaxXp(50)
                    .playerRank(null)
                    .role(userRepository.count() < 1 ? Role.ROLE_ADMIN : Role.ROLE_USER)
                    .xp(0)
                    .created_at(LocalDateTime.now())
                    .build();
            user = userRepository.save(user);
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            String token = jwtService.generateToken(authToken);
            SecurityContextHolder.getContext().setAuthentication(authToken);
            userAuditService.logUserLogin(user,request);
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
    public User registerOauthUser(String email, String name, String picture) {
        User user = User.builder()
                .created_at(LocalDateTime.now())
                .isPremium(false)
                .playerRank(null)
                .email(email)
                .username(name.replace(" ",""))
                .isBlocked(false)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .role(Role.ROLE_USER)
                .level(1)
                .xp(0)
                .currentMaxXp(50)
                .profileImageUrl(picture).build();
        return userRepository.save(user);
    }

    @Override
    public UserAuthInfo getUserAuthInfoFromEmail(String email) {
       User user = userRepository.findByEmail(email);
       return new UserAuthInfo(user.getUsername(),user.getRole());
    }

    @Override
    public User getUsernameFromUserEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public String authenticateUser(LoginRequest LoginRequest, HttpServletRequest request) {
        String username = LoginRequest.getUsername();
        String password = LoginRequest.getPassword();
        try {
           Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
           String token = jwtService.generateToken(authentication);
            User user = userRepository.findByUsername(username);
            userAuditService.logUserLogin(user,request);
            return token;
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
    @Transactional
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
        updateUserRank(user);
        if(user.getClan() != null)
            rabbitTemplate.convertAndSend(MQConfig.CLAN_LEVEL_EXCHANGE,
                MQConfig.CLAN_LEVEL_ROUTING_KEY,new UserLevelChange(increaseXpRequest.getXp(),user.getClan()));
    }


    @RabbitListener(queues = "user_queue", ackMode = "MANUAL")
    public   void changeUserPremiumStatus(ChangeUserPremiumStatusResponse response, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
          Optional<User> userOptional = userRepository.findById(response.getUserId());
            Supplier<? extends RuntimeException> noUserException =
                    () -> new UserNotFoundException("User not found with ID: " + response.getUserId());
            log.info("Changing User premium status : {}",response);
          User user = userOptional.orElseThrow(noUserException);
          user.setPremium(response.getStatus());
          userRepository.save(user);
          channel.basicAck(tag, false);
        }
        catch (UserNotFoundException e){
            log.warn("User not found ");
            throw e;
        }
        catch (Exception e){
            log.error(e.getMessage());
            channel.basicNack(tag, false, true);
        }
    }
    private void levelUp(User user) {
        user.setLevel(user.getLevel() + 1);
        user.setCurrentMaxXp(user.getLevel() * 50);
        user.setXp(0);
    }

    @Override
    public UserDto getUserData(HttpServletRequest request) {
        Long userId = Long.valueOf(jwtService.getUserIdFromRequest(request));
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() ->  new UserNotFoundException("No user with userId "+userId));
        return new UserDto(user);
    }

    @Override
    public List<LeaderboardDto> fetchGlobalLeaderboardUserInfos() {
        PageRequest pageRequest = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "level", "xp"));
        Page<User> topUsers = userRepository.findByIsDeletedFalseOrderByLevelDescXpDesc(pageRequest);
        return topUsers.stream().map(LeaderboardDto::new).toList();
    }
    @Override
    public List<LeaderboardDto> fetchFriendLeaderboardUserInfos(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with userId ("+userId+") is not present !!!"));
        List<User> friends = friendRepository.findFriendsByUserId(userId);
        friends.add(user);
        return friends.stream()
                .map(LeaderboardDto::new)
                .sorted((a, b) -> {
                    if (b.getLevel() == a.getLevel()) {
                        return Integer.compare(b.getXp(), a.getXp());
                    } else {
                        return Integer.compare(b.getLevel(), a.getLevel());
                    }
                })
                .toList();
    }

    @Override
    public void changeUsername(ChangeUsernameRequest changeUsernameRequest, long userId) {

    }

    @Override
    public void changeUserProfileImage(ChangeProfileImageRequest changeProfileImageRequest, long userId) {

    }
    @Override
    public UserDeviceTokenResponse getUserDeviceToken(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->  new UserNotFoundException("No user with userId "+userId));
        return new UserDeviceTokenResponse(user.getDeviceToken());
    }

    @Override
    public List<FriendRequestsResponseDto> getAllPendingFriendRequests(long userId) {
        userRepository.findById(userId).orElseThrow(() ->  new UserNotFoundException("No user with userId "+userId));
        List<FriendRequest> friendRequestList = friendRequestRepository.findByReceiverUserId(userId);
        return friendRequestList.stream().map(FriendRequestsResponseDto::new).toList();
    }

    @Override
    @Transactional
    public void acceptFriendRequest(Long senderId, long userId) {
        User friend1 = userRepository.findById(senderId).orElseThrow(() ->  new UserNotFoundException("No Sender user with userId "+userId));
        User friend2 = userRepository.findById(userId).orElseThrow(() ->  new UserNotFoundException("No user with userId "+userId));
        FriendRequest friendRequest = friendRequestRepository.findBySenderUserIdAndReceiverUserId(senderId, userId);
        if(Objects.isNull(friendRequest)){
            throw new FriendRequestException("Friend request does not exists");
        }
        Friend friend = Friend.builder()
                .friend1(friend1).friend2(friend2)
                .build();
        friendRepository.save(friend);
        friendRequestRepository.delete(friendRequest);
    }
    @Override
    @Transactional
    public void rejectFriendRequest(Long senderId, long userId) {
        userRepository.findById(userId).orElseThrow(() ->  new UserNotFoundException("No user with userId "+userId));
        userRepository.findById(senderId).orElseThrow(() ->  new UserNotFoundException("No Sender user with userId "+userId));
        FriendRequest friendRequest = friendRequestRepository.findBySenderUserIdAndReceiverUserId(senderId, userId);
        if(friendRequest == null)
            throw new FriendRequestException("No friend requests are present !!!");
        friendRequestRepository.delete(friendRequest);
    }

    @Override
    public List<UserDto> getAllFriends(long userId) {
        userRepository.findById(userId).orElseThrow(() ->  new UserNotFoundException("No user with userId "+userId));
        List<User> friends = friendRepository.findFriendsByUserId(userId);
        return friends.stream().map(UserDto::new).toList();
    }

    @Override
    public FriendStatusResponse checkFriendStatus(long currentUserId, long userId) {
        if(!userRepository.existsById(currentUserId)){
            throw new InvalidInputException("User with "+currentUserId+" Does not exists !!");
        }
        if(!userRepository.existsById(userId)){
            throw new InvalidInputException("User with "+userId+" Does not exists !!");
        }
        if(currentUserId == userId){
            throw new FriendRequestException("Both users are same !!!");
        }
        if(friendRepository.areFriends(currentUserId,userId)){
            return new FriendStatusResponse("FRIENDS");
        }
        List<FriendRequest> existingRequest = friendRequestRepository.findFriendRequests(currentUserId,userId);
        if(existingRequest==null || existingRequest.isEmpty()){
            return new FriendStatusResponse("NOT_FRIENDS");
        }
        if(existingRequest.get(0).getSender().getUserId() == currentUserId)
                return new FriendStatusResponse("PENDING_REQUEST");
        else{
                return new FriendStatusResponse("PENDING");
        }
    }

    @Override
    public AddUserClanResponse addClan(Long userId, Long clanId) {
        User user = userRepository.findById(userId).orElseThrow(() ->  new UserNotFoundException("No user with userId : "+userId));
        if(user.getClan() != null){
            throw new UserClanException("user is already present in a clan , clanId : "+user.getClan());
        }
        user.setClan(clanId);
        userRepository.save(user);
        return new AddUserClanResponse(user.getXp() + user.getLevel()*50L);
    }

    @Override
    public AddUserClanResponse removeClan(Long userId, Long clanId) {
        User user = userRepository.findById(userId).orElseThrow(() ->  new UserNotFoundException("No user with userId : "+userId));
        if(user.getClan() == null){
            throw new UserClanException("user is not present in the clan");
        }
        user.setClan(null);
        userRepository.save(user);
        return new AddUserClanResponse(user.getXp() + user.getLevel()*50L);
    }

    @Override
    public List<UserDto> getUsersSortedForClanLeaderboard(List<Long> userIds) {
        return userRepository.findAllById(userIds).stream().map(UserDto::new).sorted((a, b) -> {
                    if (b.getLevel() == a.getLevel()) {
                        return Integer.compare(b.getXp(), a.getXp());
                    } else {
                        return Integer.compare(b.getLevel(), a.getLevel());
                    }
                })
                .toList();
    }

    @Override
    @Transactional
    public void removeClanFromUsers(Long clanId) {
        List<User> users = userRepository.findAllByClan(clanId);
        users.forEach(user -> {
            user.setClan(null);
        });
        userRepository.saveAll(users);
    }

    @Override
    public UserCountResponse getUsersCount() {
        Long userCount = userRepository.count();
        return new UserCountResponse(userCount);
    }

    @Override
    @Transactional
    public void removeFriend(Long friendUserId, long userId) {
        if(friendUserId == userId){
            throw new InvalidInputException("Both users are same !!!");
        }
        Friend friend = friendRepository.findFriendship(friendUserId,userId);
        if(friend == null){
            throw new FriendRequestException("Users are not friends");
        }
        friendRepository.delete(friend);
    }
    @Override
    @Transactional
    public void sendFriendRequest(Long senderId, Long receiverId) {
        if(senderId.equals(receiverId)){
            throw new InvalidInputException("Both sender Id and receiver Id are same");
        }
        User sender = userRepository.findById(senderId).orElseThrow(() -> new UserNotFoundException("Sender User not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new UserNotFoundException("Receiver User not found"));

        if(friendRepository.areFriends(senderId,receiverId)){
            throw new FriendRequestException("Users are already friends");
        }
        FriendRequest existingRequest = friendRequestRepository.findBySenderUserIdAndReceiverUserId(senderId, receiverId);
        if (existingRequest != null) {
                throw new FriendRequestException("Friend request is already pending !!!");
        }
        FriendRequest friendRequest = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .build();
        friendRequestRepository.save(friendRequest);
    }

    @Override
    public List<UserDto> searchUsersContainingUsername(String username) {
        List<User> users = userRepository.findByUsernameStartingWithAndIsBlockedFalse(username);
        if(users == null || users.isEmpty()){
            return null;
        }
        return users.stream().map(UserDto::new).toList();
    }




    @Override
    @Transactional
    public void updateUserDeviceToken(UserDeviceTokenRequest userDeviceTokenRequest) {
        Long userId = userDeviceTokenRequest.getUserId();
        String deviceToken = userDeviceTokenRequest.getDeviceToken();
        User user = userRepository.findById(userId).orElseThrow(() ->  new UserNotFoundException("No user with userId "+userId));
        user.setDeviceToken(deviceToken);
        userRepository.save(user);
    }
    @Override
    public boolean setResponseCookieFromToken(String jwtToken, HttpServletResponse response) {
        String encodedToken = Base64.getEncoder().encodeToString(jwtToken.getBytes());
        Cookie cookie =  new Cookie("userToken", encodedToken);
        cookie.setMaxAge(60 * 60 * 24 * 2);  // 2 days
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        String cookieHeader = String.format("userToken=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=None",
                encodedToken,cookie.getMaxAge());
        log.info("Cookie header : {}",cookieHeader);
        response.addHeader("Set-Cookie", cookieHeader);
        return true;
    }


    private void updateUserRank(User user) {
        userRepository.updateAllUsersRank();
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
