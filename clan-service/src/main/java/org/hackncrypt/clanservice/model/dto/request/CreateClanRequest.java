package org.hackncrypt.clanservice.model.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateClanRequest {
    private String name;
    private String description;
}
