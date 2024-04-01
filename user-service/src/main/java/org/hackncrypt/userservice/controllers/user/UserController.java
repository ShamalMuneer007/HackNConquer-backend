package org.hackncrypt.userservice.controllers.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.userservice.model.dto.request.IncreaseXpRequest;
import org.hackncrypt.userservice.model.dto.response.ApiSuccessResponse;
import org.hackncrypt.userservice.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController("/api/v1/user")
@PreAuthorize("hasRole('USER')")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/increase-xp")
    public ResponseEntity<ApiSuccessResponse> increaseUserLevel(@RequestBody IncreaseXpRequest increaseXpRequest, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        userService.increaseUserXp(increaseXpRequest);
        return ResponseEntity.ok(new ApiSuccessResponse("XP increased", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }

}
