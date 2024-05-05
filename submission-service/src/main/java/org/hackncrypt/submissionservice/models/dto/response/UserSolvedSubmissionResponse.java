package org.hackncrypt.submissionservice.models.dto.response;



import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSolvedSubmissionResponse {
    private String difficulty;
    private String problemName;
    private float acceptanceRate;
    private LocalDate solvedAt;
    private String solutionCode;
    private Integer problemLevel;
    private List<String> categories;
    private String problemId;
    private Double bestRuntime;
    private Double bestMemory;
    private Long problemNo;
    private Integer languageId;
}
