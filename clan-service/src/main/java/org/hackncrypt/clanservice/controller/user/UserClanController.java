package org.hackncrypt.clanservice.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.clanservice.model.dto.ClanDto;
import org.hackncrypt.clanservice.model.dto.response.ApiSuccessResponse;
import org.hackncrypt.clanservice.service.ClanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@RequiredArgsConstructor
public class UserClanController {
    private final ClanService clanService;


    @PostMapping(path = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiSuccessResponse> createClan(@RequestParam("image") MultipartFile image,
                                                         @RequestParam("name") String name,
                                                         @RequestParam("description") String description,
                                                         HttpServletRequest request) throws IOException {
        String requestURI = request.getRequestURI();
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        String authHeader = request.getHeader("Authorization");
        clanService.createClan(name,description,userId,image,authHeader);
        return ResponseEntity.ok(new ApiSuccessResponse("Clan created successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }
    @PatchMapping("/join/{clanId}")
    public ResponseEntity<ApiSuccessResponse> joinClan(@PathVariable Long clanId, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        String authHeader = request.getHeader("Authorization");
        clanService.joinClan(clanId,userId,authHeader);
        return ResponseEntity.ok(new ApiSuccessResponse("Added to clan successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }
    @PatchMapping("/leave/{clanId}")
    public ResponseEntity<ApiSuccessResponse> leaveClan(@PathVariable Long clanId, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        String authHeader = request.getHeader("Authorization");
        clanService.leaveClan(clanId,userId,authHeader);
        return ResponseEntity.ok(new ApiSuccessResponse("User Left the clan successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }
    @PatchMapping("/member/remove/{clanId}/{memberId}")
    public ResponseEntity<ApiSuccessResponse> leaveClan(@PathVariable("clanId") Long clanId,@PathVariable("memberId") Long memberId, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        String authHeader = request.getHeader("Authorization");
        clanService.leaveClan(clanId,memberId,authHeader);
        return ResponseEntity.ok(new ApiSuccessResponse("Member Left the clan successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }

    @DeleteMapping("/disband/{clanId}")
    public ResponseEntity<ApiSuccessResponse> disbandClan(@PathVariable Long clanId,HttpServletRequest request){
        String requestURI = request.getRequestURI();
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        String authHeader = request.getHeader("Authorization");
        clanService.disband(clanId,userId,authHeader);
        return ResponseEntity.ok(new ApiSuccessResponse("clan disband done successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }
    @PatchMapping("/owner/handle-to/{toUserId}/{clanId}")
    public ResponseEntity<ApiSuccessResponse> switchOwnership(@PathVariable("toUserId") Long toUserId,@PathVariable("clanId") Long clanId, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        long userId = Long.parseLong((String)request.getAttribute("userId"));
        String authHeader = request.getHeader("Authorization");
        clanService.switchOwnership(clanId,toUserId,userId,authHeader);
        return ResponseEntity.ok(new ApiSuccessResponse("Left the clan successfully", HttpStatus.OK.value(), LocalDate.now(),requestURI));
    }
}
