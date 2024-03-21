package org.hackncrypt.userservice.controllers.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.userservice.model.dto.UserDto;
import org.hackncrypt.userservice.model.dto.response.GetAllUserInfoResponse;
import org.hackncrypt.userservice.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping("/get-all-user")
    public ResponseEntity<GetAllUserInfoResponse> getAllUserInfo(@RequestParam(defaultValue = "1") String page,
                                                                 @RequestParam(defaultValue = "10") String size){
        Page<UserDto> users = userService.getAllUsers(Integer.parseInt(page), Integer.parseInt(size));

        return ResponseEntity.ok(new GetAllUserInfoResponse(users, HttpStatus.OK.value()));
    }
}
