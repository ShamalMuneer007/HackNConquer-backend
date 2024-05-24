package org.hackncrypt.userservice.controllers.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.userservice.model.dto.LeaderboardDto;
import org.hackncrypt.userservice.model.dto.request.*;
import org.hackncrypt.userservice.model.dto.UserDto;
import org.hackncrypt.userservice.model.dto.response.AddUserClanResponse;
import org.hackncrypt.userservice.model.dto.response.ApiSuccessResponse;
import org.hackncrypt.userservice.model.dto.response.FriendRequestsResponseDto;
import org.hackncrypt.userservice.model.dto.response.FriendStatusResponse;
import org.hackncrypt.userservice.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@PreAuthorize("hasRole('USER')")
@Slf4j
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/get-friends-leaderboard")
    public ResponseEntity<List<LeaderboardDto>> getGlobalLeaderboard(HttpServletRequest request){
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        return ResponseEntity.ok(userService.fetchFriendLeaderboardUserInfos(userId));
    }
    @PostMapping("/increase-xp")
    public ResponseEntity<ApiSuccessResponse> increaseUserLevel(@RequestBody IncreaseXpRequest increaseXpRequest, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        userService.increaseUserXp(increaseXpRequest);
        return ResponseEntity.ok(new ApiSuccessResponse("XP increased", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }
    @PatchMapping("/change-username")
    public ResponseEntity<ApiSuccessResponse> changeUsername(@RequestBody ChangeUsernameRequest changeUsernameRequest, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        userService.changeUsername(changeUsernameRequest,userId);
        return ResponseEntity.ok(new ApiSuccessResponse("username changed successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }
    @PostMapping("/set-device-token")
    public ResponseEntity<ApiSuccessResponse> updateUserDeviceToken(@RequestBody UserDeviceTokenRequest userDeviceTokenRequest,
                                                                    HttpServletRequest request){
        String requestURI = request.getRequestURI();
        userService.updateUserDeviceToken(userDeviceTokenRequest);
        return ResponseEntity.ok(new ApiSuccessResponse("user device token updated successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }

    @PatchMapping("/change-profile-image")
    public ResponseEntity<ApiSuccessResponse> changeProfileImage(@RequestBody ChangeProfileImageRequest changeProfileImageRequest, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        userService.changeUserProfileImage(changeProfileImageRequest,userId);
        return ResponseEntity.ok(new ApiSuccessResponse("username changed successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }
    @GetMapping("/friends/get-friends")
    public ResponseEntity<List<UserDto>> getAllFriends(HttpServletRequest request){
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        return ResponseEntity.ok(userService.getAllFriends(userId));
    }
    @PostMapping("/friends/send-request")
    public ResponseEntity<ApiSuccessResponse> sendFriendRequest(@RequestBody AddFriendRequestDto addFriendRequestDto,HttpServletRequest request){
        String requestURI = request.getRequestURI();
        userService.sendFriendRequest(addFriendRequestDto.getSenderId(),addFriendRequestDto.getReceiverId());
        return ResponseEntity.ok(new ApiSuccessResponse("Friend request send successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }
    @GetMapping("/friends/get-status/{userId}")
    public ResponseEntity<FriendStatusResponse> checkFriendStatus(@PathVariable Long userId, HttpServletRequest request){
        long currentUserId = Long.parseLong((String)request.getAttribute("userId"));
        return ResponseEntity.ok(userService.checkFriendStatus(currentUserId,userId));
    }
    @GetMapping("friends/get-requests")
    public ResponseEntity<List<FriendRequestsResponseDto>> getAllFriendRequests(HttpServletRequest request){
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        return ResponseEntity.ok(userService.getAllPendingFriendRequests(userId));
    }
    @PostMapping("friends/accept-request/{senderId}")
    public ResponseEntity<ApiSuccessResponse> acceptFriendRequest(HttpServletRequest request, @PathVariable Long senderId){
        String requestURI = request.getRequestURI();
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        userService.acceptFriendRequest(senderId,userId);
        return ResponseEntity.ok(new ApiSuccessResponse("Friend request accepted successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }
    @DeleteMapping("friends/reject-request/{senderId}")
    public ResponseEntity<ApiSuccessResponse> rejectFriendRequest(HttpServletRequest request, @PathVariable Long senderId){
        String requestURI = request.getRequestURI();
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        userService.rejectFriendRequest(senderId,userId);
        return ResponseEntity.ok(new ApiSuccessResponse("Friend request rejected successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }
    @DeleteMapping("friends/remove-friend/{friendId}")
    public ResponseEntity<ApiSuccessResponse> removeFriend(@PathVariable Long friendId,HttpServletRequest request){
        String requestURI = request.getRequestURI();
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        userService.removeFriend(friendId,userId);
        return ResponseEntity.ok(new ApiSuccessResponse("Friend removed successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }
    @PutMapping("add-clan/{userId}/{clanId}")
    public ResponseEntity<AddUserClanResponse> addUserClan(@PathVariable("userId") Long userId,@PathVariable("clanId") Long clanId,HttpServletRequest request){
        String requestURI = request.getRequestURI();
        AddUserClanResponse response = userService.addClan(userId,clanId);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/remove-clan/{userId}/{clanId}")
    public ResponseEntity<AddUserClanResponse> removeUserClan(@PathVariable("userId") Long userId,@PathVariable("clanId") Long clanId,HttpServletRequest request){
        String requestURI = request.getRequestURI();
        AddUserClanResponse addUserClanResponse = userService.removeClan(userId,clanId);
        return ResponseEntity.ok(addUserClanResponse);
    }
    @PutMapping("/clan-disband/{clanId}")
    ResponseEntity<ApiSuccessResponse> removeClanFromUsers(@PathVariable Long clanId,HttpServletRequest request){
        String requestURI = request.getRequestURI();
        userService.removeClanFromUsers(clanId);
        return ResponseEntity.ok(new ApiSuccessResponse("Removed clan from users !!!", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }

    @GetMapping("/fetch-userdata")
    public ResponseEntity<UserDto> fetchAuthenticatedUserData(HttpServletRequest request){
        return ResponseEntity.ok(userService.getUserData(request));
    }

}
