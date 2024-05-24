package org.hackncrypt.userservice.controllers.admin;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.userservice.controllers.UserAuditDto;
import org.hackncrypt.userservice.model.dto.UserDto;
import org.hackncrypt.userservice.model.dto.response.ApiSuccessResponse;
import org.hackncrypt.userservice.model.dto.response.GetAllUserInfoResponse;
import org.hackncrypt.userservice.service.UserAuditService;
import org.hackncrypt.userservice.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final UserAuditService userAuditService;
    @GetMapping("/get-all-user")
    public ResponseEntity<GetAllUserInfoResponse> getAllUserInfo(@RequestParam(defaultValue = "1") String page,
                                                                 @RequestParam(defaultValue = "10") String size){
        Page<UserDto> users = userService.getAllUsers(Integer.parseInt(page), Integer.parseInt(size));

        return ResponseEntity.ok(new GetAllUserInfoResponse(users, HttpStatus.OK.value()));
    }
    @GetMapping("/audit-logs")
    public ResponseEntity<List<UserAuditDto>> getUserAuditLogs(){
        return ResponseEntity.ok((userAuditService.getUserAuditLogs()));
    }
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<ApiSuccessResponse> deleteUser(@PathVariable Long userId,
                                                         HttpServletRequest request){
        userService.deleteUserByUserId(userId);
        return ResponseEntity
                .ok(new ApiSuccessResponse("Deleted user successfully",
                        HttpStatus.OK.value(), LocalDate.now(),request.getPathInfo()));
    }
}
