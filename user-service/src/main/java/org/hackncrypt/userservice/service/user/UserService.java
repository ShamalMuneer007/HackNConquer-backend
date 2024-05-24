package org.hackncrypt.userservice.service.user;

import jakarta.servlet.http.HttpServletRequest;
import org.hackncrypt.userservice.model.dto.response.*;
import org.hackncrypt.userservice.model.dto.LeaderboardDto;
import org.hackncrypt.userservice.model.dto.UserDto;
import org.hackncrypt.userservice.model.dto.auth.UserAuthInfo;
import org.hackncrypt.userservice.model.dto.auth.request.LoginRequest;
import org.hackncrypt.userservice.model.dto.auth.request.RegisterRequest;
import org.hackncrypt.userservice.model.dto.request.ChangeProfileImageRequest;
import org.hackncrypt.userservice.model.dto.request.ChangeUsernameRequest;
import org.hackncrypt.userservice.model.dto.request.IncreaseXpRequest;
import org.hackncrypt.userservice.model.dto.request.UserDeviceTokenRequest;
import org.hackncrypt.userservice.model.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    String registerUser(RegisterRequest userRegisterDto, HttpServletRequest request);

    void sendRegistrationEmailOtp(RegisterRequest email);

    boolean validateUserOtp(String email, String otp);

    boolean existsByEmail(String email);

    User registerOauthUser(String email, String name, String picture);

    UserAuthInfo getUserAuthInfoFromEmail(String email);

    User getUsernameFromUserEmail(String email);

    String authenticateUser(LoginRequest userLoginRequestDto, HttpServletRequest request);

    Page<UserDto> getAllUsers(int page, int size);

    void deleteUserByUserId(Long userId);

    void increaseUserXp(IncreaseXpRequest increaseXpRequest);

    UserDto getUserData(HttpServletRequest request);

    List<LeaderboardDto> fetchGlobalLeaderboardUserInfos();

    List<LeaderboardDto> fetchFriendLeaderboardUserInfos(Long userId);

    void changeUsername(ChangeUsernameRequest changeUsernameRequest, long userId);

    void changeUserProfileImage(ChangeProfileImageRequest changeProfileImageRequest, long userId);


    void removeFriend(Long friendUserId, long userId);

    List<UserDto> searchUsersContainingUsername(String username);


    void sendFriendRequest(Long senderId, Long receiverId);

    void updateUserDeviceToken(UserDeviceTokenRequest userDeviceTokenRequest);

    UserDeviceTokenResponse getUserDeviceToken(Long userId);

    List<FriendRequestsResponseDto> getAllPendingFriendRequests(long userId);

    void acceptFriendRequest(Long senderId, long userId);

    @Transactional
    void rejectFriendRequest(Long senderId, long userId);


    List<UserDto> getAllFriends(long userId);

    FriendStatusResponse checkFriendStatus(long currentUserId, long userId);

    AddUserClanResponse addClan(Long userId, Long clanId);

    AddUserClanResponse removeClan(Long userId, Long clanId);

    List<UserDto> getUsersSortedForClanLeaderboard(List<Long> userIds);

    void removeClanFromUsers(Long clanId);

    UserCountResponse getUsersCount();
}
