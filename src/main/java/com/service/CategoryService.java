package com.service;

import com.dto.request.CategoryRequest;
import com.dto.response.CategoryResponse;
import com.entity.Category;
import com.mapper.CategoryMapper;
import com.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    public CategoryResponse create(CategoryRequest request) {
        Category category = mapper.toEntity(request);
        return mapper.toResponse(repository.save(category));
    }

    public List<CategoryResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public CategoryResponse getById(UUID id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return mapper.toResponse(category);
    }

    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        mapper.updateCategory(category, request);
        return mapper.toResponse(repository.save(category));
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}