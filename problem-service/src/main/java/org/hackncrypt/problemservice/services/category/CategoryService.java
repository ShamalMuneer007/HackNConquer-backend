package org.hackncrypt.problemservice.services.category;

import org.hackncrypt.problemservice.model.dto.CategoryDto;
import org.hackncrypt.problemservice.model.dto.request.AddCategoryRequest;
import org.hackncrypt.problemservice.model.entities.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategoriesOfCategoryNames(List<String> categoryNames);

    Page<CategoryDto> getAllCategories(int page, int size);

    void addCategory(AddCategoryRequest addCategoryRequest);

    void removeCategory(String categoryId);
}
