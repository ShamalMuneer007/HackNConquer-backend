package org.hackncrypt.userservice.controllers.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.userservice.model.dto.UserDto;
import org.hackncrypt.userservice.model.dto.request.IncreaseXpRequest;
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
    @GetMapping("/fetch-userdata")
    public ResponseEntity<UserDto> getUserData(HttpServletRequest request){
        return ResponseEntity.ok(userService.getUserData(request));
    }

}
