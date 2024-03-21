package org.hackncrypt.problemservice.controllers.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.problemservice.annotations.Authorized;
import org.hackncrypt.problemservice.model.dto.response.ApiSuccessResponse;
import org.hackncrypt.problemservice.model.dto.request.AddCategoryRequest;
import org.hackncrypt.problemservice.services.category.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@Authorized("ROLE_ADMIN")
@RequestMapping("/api/v1/admin")
public class AdminCategoryController {
    private final CategoryService categoryService;
    @PostMapping("/add-category")
    public ResponseEntity<ApiSuccessResponse> addProblemCategory(@RequestBody AddCategoryRequest addCategoryRequest){
        categoryService.addCategory(addCategoryRequest);
        return ResponseEntity.ok(new ApiSuccessResponse("Category added successfully", HttpStatus.OK.value()));
    }
    @DeleteMapping("/delete-category/{categoryId}")
    public ResponseEntity<ApiSuccessResponse> deleteCategory(@PathVariable String categoryId){
        categoryService.removeCategory(categoryId);
        return ResponseEntity.ok(new ApiSuccessResponse("category removed successfully !!", HttpStatus.OK.value()));
    }
}
