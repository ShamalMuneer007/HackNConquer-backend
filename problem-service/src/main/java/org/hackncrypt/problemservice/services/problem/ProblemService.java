package org.hackncrypt.problemservice.services.problem;

import org.hackncrypt.problemservice.model.dto.ProblemDto;
import org.hackncrypt.problemservice.model.dto.request.PatchProblemRequest;
import org.hackncrypt.problemservice.model.dto.request.AddProblemRequest;
import org.hackncrypt.problemservice.model.dto.request.ProblemVerificationRequest;
import org.hackncrypt.problemservice.model.dto.response.ProblemVerificationResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProblemService {
    ProblemVerificationResponse verifyProblem(ProblemVerificationRequest problemVerificationRequest) throws Exception;

    void addProblem(AddProblemRequest addProblemRequest);

    void updateProblemDetails(long problemNo,PatchProblemRequest patchProblemRequest);

    Page<ProblemDto> getAllProblem(int page, int size);

    void deleteProblem(String problemId);

    ProblemDto getProblemById(String problemId);
}
