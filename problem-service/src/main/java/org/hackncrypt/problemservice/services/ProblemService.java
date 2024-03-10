package org.hackncrypt.problemservice.services;

import org.hackncrypt.problemservice.model.dto.Request.ProblemVerificationRequest;
import org.hackncrypt.problemservice.model.dto.Response.ProblemVerificationResponse;

public interface ProblemService {
    ProblemVerificationResponse verifyProblem(ProblemVerificationRequest problemVerificationRequest) throws Exception;
}
