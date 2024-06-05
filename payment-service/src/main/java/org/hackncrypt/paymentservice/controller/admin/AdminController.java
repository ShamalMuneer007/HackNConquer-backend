package org.hackncrypt.paymentservice.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.paymentservice.annotations.Authorized;
import org.hackncrypt.paymentservice.model.dto.GetTotalRevenueResponse;
import org.hackncrypt.paymentservice.model.dto.response.GetRevenueResponse;
import org.hackncrypt.paymentservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Authorized("ROLE_ADMIN")
@RestController
@RequestMapping("/api/v1/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminController {
    private final PaymentService paymentService;
    @GetMapping("/revenue")
    public ResponseEntity<List<GetRevenueResponse>> getRevenue(@RequestParam(value = "interval") String interval, HttpServletRequest request){
        log.info("REQUEST PARAM GET REVENUE : {}",interval);
        return ResponseEntity.ok(paymentService.getAllSubscriptions(interval));
    }
    @GetMapping("/total-revenue")
    public ResponseEntity<GetTotalRevenueResponse> getTotalRevenue(HttpServletRequest request){
        return ResponseEntity.ok(paymentService.getTotalRevenue());
    }
    @GetMapping("/subscriptions/active")
    public ResponseEntity<Long> getActiveSubscriptions(){
        return ResponseEntity.ok(paymentService.getAllActiveSubscriptions());
    }

}
