package org.hackncrypt.submissionservice.models.dto.response;

import lombok.*;
import org.hackncrypt.problemservice.model.dto.testCases.AcceptedCase;
import org.hackncrypt.problemservice.model.dto.testCases.RejectedCase;
import org.hackncrypt.submissionservice.enums.SubmissionStatus;
import org.hackncrypt.submissionservice.models.entities.Submission;

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
