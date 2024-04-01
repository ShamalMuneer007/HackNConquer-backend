package org.hackncrypt.submissionservice.models.dto.response;

import lombok.*;
import org.hackncrypt.submissionservice.enums.SubmissionStatus;
import org.hackncrypt.submissionservice.models.dto.testCases.AcceptedCase;
import org.hackncrypt.submissionservice.models.dto.testCases.RejectedCase;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SubmitSolutionResponse {
    private SubmissionStatus submissionStatus;
    private Double averageTime;
    private Double averageMemory;
    private List<RejectedCase> rejectedCases;
    private List<AcceptedCase> acceptedCases;
    private int totalTestCases;
    public SubmitSolutionResponse(RunAndTestResponse solutionResponse){
        this.acceptedCases = solutionResponse.getAcceptedCases();
        this.rejectedCases = solutionResponse.getRejectedCases();
        this.averageMemory = solutionResponse.getAverageMemory();
        this.averageTime = solutionResponse.getAverageTime();
        this.submissionStatus = solutionResponse.getSubmissionStatus();
        this.totalTestCases = solutionResponse.getTotalTestCases();
    }
}
