package org.hackncrypt.userservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApiSuccessResponse {
    private String message;
    private int status;
    private LocalDate timeStamp;
    private String requestPath;
}
