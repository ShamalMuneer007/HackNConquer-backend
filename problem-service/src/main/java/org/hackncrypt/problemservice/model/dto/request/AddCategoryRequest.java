package org.hackncrypt.problemservice.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Validated
public class AddCategoryRequest {
    @NotNull
    @Size(min = 5)
    private String categoryName;
}
