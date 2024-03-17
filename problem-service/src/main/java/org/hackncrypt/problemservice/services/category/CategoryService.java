package org.hackncrypt.problemservice.services.category;

import org.hackncrypt.problemservice.model.dto.response.GetCategoryResponse;
import org.hackncrypt.problemservice.model.dto.request.AddCategoryRequest;
import org.hackncrypt.problemservice.model.entities.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategoriesOfCategoryNames(List<String> categoryNames);

    List<GetCategoryResponse> getAllCategories();

    void addCategory(AddCategoryRequest addCategoryRequest);

    void removeCategory(String categoryId);
}
