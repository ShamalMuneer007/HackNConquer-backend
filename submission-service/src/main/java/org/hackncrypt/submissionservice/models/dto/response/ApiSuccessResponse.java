package org.hackncrypt.submissionservice.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiSuccessResponse {
    private String message;
    private int status;
}
