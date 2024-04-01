package org.hackncrypt.testservice.models.dto.testCases;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RejectedCase {
    private String input;
    private String output;
    private String expectedOutput;
}
