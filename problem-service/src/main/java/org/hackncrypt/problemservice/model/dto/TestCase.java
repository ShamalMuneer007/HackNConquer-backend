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

}
