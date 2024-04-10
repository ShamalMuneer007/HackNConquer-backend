package org.hackncrypt.problemservice.controllers.user;

import lombok.RequiredArgsConstructor;
import org.hackncrypt.problemservice.model.dto.ProblemDto;
import org.hackncrypt.problemservice.model.dto.request.ChangeProblemAcceptanceRateRequest;
import org.hackncrypt.problemservice.model.dto.response.ApiSuccessResponse;
import org.hackncrypt.problemservice.services.problem.ProblemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserProblemController {
    private final ProblemService problemService;
    @PostMapping("/change-acceptance-rate")
    public ResponseEntity<ApiSuccessResponse> changeProblemAcceptanceRate(@RequestBody ChangeProblemAcceptanceRateRequest changeProblemAcceptanceRateRequest){
        problemService.changeProblemAcceptanceRate(changeProblemAcceptanceRateRequest);
        ApiSuccessResponse response  = new ApiSuccessResponse("Problem acceptance rate changed successfully", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/get-problems-by-problemid-list")
    ResponseEntity<List<ProblemDto>> getProblemsByProblemIdList(@RequestParam("problemIdList") List<String> problemIdList){
        return ResponseEntity.ok(problemService.getAllProblemsInProblemIdList(problemIdList));
    }
}
