package org.hackncrypt.problemservice.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackncrypt.problemservice.model.dto.CategoryDto;
import org.hackncrypt.problemservice.model.dto.ProblemDto;
import org.hackncrypt.problemservice.model.dto.response.GetAllCategoryResponse;
import org.hackncrypt.problemservice.model.dto.response.GetCategoryResponse;
import org.hackncrypt.problemservice.services.category.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CategoryController {
    private final CategoryService categoryService;
    @GetMapping("/get-all-category")
    public ResponseEntity<GetAllCategoryResponse> getAllCategories(@RequestParam(defaultValue = "1") String page,
                                                                   @RequestParam(defaultValue = "10") String size){
        Page<CategoryDto> categories =  categoryService.getAllCategories(Integer.parseInt(page),Integer.parseInt(size));
        return ResponseEntity.ok(new GetAllCategoryResponse(categories,HttpStatus.OK.value()));
    }
}
