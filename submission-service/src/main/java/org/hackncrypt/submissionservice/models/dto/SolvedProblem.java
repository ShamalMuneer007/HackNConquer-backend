package org.hackncrypt.submissionservice.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolvedProblem {
    private String problemDifficulty;
    private String problemName;
    private LocalDate solvedAt;
    private String solutionCode;
    private String category;
    private String problemId;
}
