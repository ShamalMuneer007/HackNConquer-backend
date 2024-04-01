package org.hackncrypt.submissionservice.models.dto.testCases;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TestCase {
    private String testCaseInput;
    private String expectedOutput;
    public void setTestCaseInput(String testCaseInput) {
        this.testCaseInput = testCaseInput != null ? testCaseInput.trim() : null;
    }
    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput != null ? expectedOutput.trim() : null;
    }
}
