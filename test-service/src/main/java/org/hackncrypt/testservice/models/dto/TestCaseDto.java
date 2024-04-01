package org.hackncrypt.testservice.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hackncrypt.testservice.models.dto.testCases.TestCase;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseDto {
    private String testCaseInput;
    private String expectedOutput;
    private int idx;

    public TestCaseDto(TestCase test, int index) {
        this.testCaseInput = test.getTestCaseInput();
        this.expectedOutput = test.getExpectedOutput();
        this.idx = index;
    }
}
