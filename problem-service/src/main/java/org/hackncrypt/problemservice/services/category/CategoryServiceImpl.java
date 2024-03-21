package org.hackncrypt.problemservice.services.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.problemservice.exceptions.business.NoSuchValueException;
import org.hackncrypt.problemservice.exceptions.business.DuplicateValueException;
import org.hackncrypt.problemservice.model.dto.CategoryDto;
import org.hackncrypt.problemservice.model.dto.response.GetCategoryResponse;
import org.hackncrypt.problemservice.model.dto.request.AddCategoryRequest;
import org.hackncrypt.problemservice.model.entities.Category;
import org.hackncrypt.problemservice.repositories.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    public List<Category> getAllCategoriesOfCategoryNames(List<String> categoryNames) {
           return categoryRepository.findAllByCategoryNameIn(categoryNames);
    }

    @Override
    public Page<CategoryDto> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Category> categoryPage = categoryRepository.findAllByIsDeletedIsFalse(pageable);
        return categoryPage.map(CategoryDto::new);
    }

    @Override
    public void addCategory(AddCategoryRequest addCategoryRequest) {
        if(categoryRepository.existsByCategoryName(addCategoryRequest.getCategoryName())){
            throw new DuplicateValueException("category already exists !!");
        }
        Category category = Category
                .builder()
                .categoryName(addCategoryRequest.getCategoryName())
                .isDeleted(false)
                .level(addCategoryRequest.getCategoryLevel())
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        categoryRepository.save(category);
    }

    @Override
    public void removeCategory(String categoryId) {
        if(!categoryRepository.existsById(categoryId)){
            throw new NoSuchValueException("category id does not exists!!!");
        }
        categoryRepository.deleteById(categoryId);
    }
}
