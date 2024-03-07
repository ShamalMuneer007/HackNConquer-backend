package org.hackncrypt.problemservice.services;

import org.hackncrypt.problemservice.model.dto.ProblemVerificationDto;
import org.hackncrypt.problemservice.model.dto.ProblemVerificationResponse;

public interface ProblemService {
    ProblemVerificationResponse verifyProblem(ProblemVerificationDto problemVerificationDto) throws Exception;
}
