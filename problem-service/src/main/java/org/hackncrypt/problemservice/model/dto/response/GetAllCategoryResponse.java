package org.hackncrypt.problemservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hackncrypt.problemservice.model.dto.CategoryDto;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetAllCategoryResponse {
    private Page<CategoryDto> categories;
    private int status;

}
