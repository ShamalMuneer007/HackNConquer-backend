package org.hackncrypt.problemservice.model.dto.testCases;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
