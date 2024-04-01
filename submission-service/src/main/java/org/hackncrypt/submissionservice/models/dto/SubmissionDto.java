package org.hackncrypt.submissionservice.models.dto;

import lombok.*;
import org.hackncrypt.submissionservice.models.entities.Submission;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SubmissionDto {
    private Double averageMemory;
    private Double averageTime;
    private String submissionStatus;
    private String submittedAt;
    public SubmissionDto(Submission submission) {
        this.averageMemory = submission.getAverageMemory();
        this.averageTime = submission.getAverageTime();
        this.submissionStatus = submission.getSubmissionStatus().name().replace('_',' ');
        this.submittedAt = submission.getSubmittedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
