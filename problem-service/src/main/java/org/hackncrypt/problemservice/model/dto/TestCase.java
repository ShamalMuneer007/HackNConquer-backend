package org.hackncrypt.problemservice.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
