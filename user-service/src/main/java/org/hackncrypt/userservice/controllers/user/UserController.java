package org.hackncrypt.userservice.controllers.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.userservice.model.dto.request.ChangeProfileImageRequest;
import org.hackncrypt.userservice.model.dto.UserDto;
import org.hackncrypt.userservice.model.dto.request.ChangeUsernameRequest;
import org.hackncrypt.userservice.model.dto.request.IncreaseXpRequest;
import org.hackncrypt.userservice.model.dto.request.UserDeviceTokenRequest;
import org.hackncrypt.userservice.model.dto.response.ApiSuccessResponse;
import org.hackncrypt.userservice.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@PreAuthorize("hasRole('USER')")
@Slf4j
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
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
    @PostMapping("/add-friend/{friendUserId}")
    public ResponseEntity<ApiSuccessResponse> addFriend(@PathVariable Long friendUserId, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        userService.addFriend(friendUserId,userId);
        return ResponseEntity.ok(new ApiSuccessResponse("username changed successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }
    @PostMapping("/set-device-token")
    public ResponseEntity<ApiSuccessResponse> updateUserDeviceToken(@RequestBody UserDeviceTokenRequest userDeviceTokenRequest,
                                                                    HttpServletRequest request){
        String requestURI = request.getRequestURI();
        userService.updateUserDeviceToken(userDeviceTokenRequest);
        return ResponseEntity.ok(new ApiSuccessResponse("user device token updated successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }

    @PostMapping("/friend-request/{friendUserId}")
    public ResponseEntity<ApiSuccessResponse> sendFriendRequest(@PathVariable Long friendUserId, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        userService.sendFriendRequest(friendUserId,userId);
        return ResponseEntity.ok(new ApiSuccessResponse("username changed successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }
    @DeleteMapping("/remove-friend/{friendUserId}")
    public ResponseEntity<ApiSuccessResponse> removeFriend(@PathVariable Long friendUserId, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        userService.removeFriend(friendUserId,userId);
        return ResponseEntity.ok(new ApiSuccessResponse("username changed successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }
    @PatchMapping("/change-profile-image")
    public ResponseEntity<ApiSuccessResponse> changeProfileImage(@RequestBody ChangeProfileImageRequest changeProfileImageRequest, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        userService.changeUserProfileImage(changeProfileImageRequest,userId);
        return ResponseEntity.ok(new ApiSuccessResponse("username changed successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }


    @GetMapping("/fetch-userdata")
    public ResponseEntity<UserDto> fetchAuthenticatedUserData(HttpServletRequest request){
        return ResponseEntity.ok(userService.getUserData(request));
    }

}
