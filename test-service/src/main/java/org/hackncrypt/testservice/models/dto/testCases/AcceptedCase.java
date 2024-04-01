package org.hackncrypt.submissionservice.models.dto.testCases;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AcceptedCase {
    @JsonProperty
    private String input;
    @JsonProperty
    private String output;
}
