package org.hackncrypt.problemservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hackncrypt.problemservice.model.entities.Category;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private String categoryId;
    private String categoryName;
    private int categoryLevel;
    public CategoryDto(Category category){
        this.categoryName = category.getCategoryName();
        this.categoryId = category.getCategoryId();
        this.categoryLevel = category.getLevel();
    }
}
