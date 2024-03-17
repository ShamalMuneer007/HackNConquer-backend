package org.hackncrypt.problemservice.services.problem;

import org.hackncrypt.problemservice.model.dto.request.PatchProblemRequest;
import org.hackncrypt.problemservice.model.dto.request.AddProblemRequest;
import org.hackncrypt.problemservice.model.dto.request.ProblemVerificationRequest;
import org.hackncrypt.problemservice.model.dto.response.ProblemVerificationResponse;

public interface ProblemService {
    ProblemVerificationResponse verifyProblem(ProblemVerificationRequest problemVerificationRequest) throws Exception;

    void addProblem(AddProblemRequest addProblemRequest);

    void updateProblemDetails(long problemNo,PatchProblemRequest patchProblemRequest);
}
