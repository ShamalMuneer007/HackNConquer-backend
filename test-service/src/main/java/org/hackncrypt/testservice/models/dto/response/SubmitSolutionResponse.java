package org.hackncrypt.testservice.models.dto.response;

import lombok.*;
import org.hackncrypt.testservice.enums.SubmissionStatus;
import org.hackncrypt.testservice.models.dto.testCases.AcceptedCase;
import org.hackncrypt.testservice.models.dto.testCases.RejectedCase;

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
    public SubmitSolutionResponse(RunAndTestResponse solutionResponse){
        this.acceptedCases = solutionResponse.getAcceptedCases();
        this.rejectedCases = solutionResponse.getRejectedCases();
        this.averageMemory = solutionResponse.getAverageMemory();
        this.averageTime = solutionResponse.getAverageTime();
        this.submissionStatus = solutionResponse.getSubmissionStatus();
    }
}
