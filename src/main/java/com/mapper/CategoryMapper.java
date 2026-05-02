package com.mapper;

import com.dto.request.CategoryRequest;
import com.dto.response.CategoryResponse;
import com.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category category);

    void updateCategory(@MappingTarget Category category, CategoryRequest request);
}