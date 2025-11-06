package org.example.tamaapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.tamaapi.domain.item.Category;
import org.example.tamaapi.dto.responseDto.category.CategoryResponse;
import org.example.tamaapi.dto.responseDto.category.ParentCategoryResponse;
import org.example.tamaapi.command.item.CategoryRepository;
import org.example.tamaapi.query.item.CategoryQueryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
//카테고리 아이템은 itemApi
public class CategoryApiController {

    private final CategoryQueryRepository categoryQueryRepository;

    @GetMapping("/api/category")
    public List<CategoryResponse> category() {
        List<Category> categories = categoryQueryRepository.findAllWithChildrenAllByParentIsNull();
        return categories.stream().map(CategoryResponse::new).toList();
    }

    @GetMapping("/api/category/parent")
    public List<ParentCategoryResponse> parentCategory() {
        List<Category> categories = categoryQueryRepository.findAllByParentIsNull();
        return categories.stream().map(ParentCategoryResponse::new).toList();
    }

    @GetMapping("/api/category/{categoryId}")
    public CategoryResponse category(@PathVariable Long categoryId) {
        Category category = categoryQueryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("존재하지않는 카테고리입니다."));
        return new CategoryResponse(category);
    }

}
