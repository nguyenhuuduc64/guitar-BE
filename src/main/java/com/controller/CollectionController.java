package com.controller;

import com.dto.request.ApiResponse;
import com.dto.request.CollectionRequest;
import com.dto.response.CollectionResponse;
import com.service.CollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService service;

    // CREATE
    @PostMapping
    public ApiResponse<CollectionResponse> create(
            @RequestBody @Valid CollectionRequest request
    ) {
        ApiResponse<CollectionResponse> response = new ApiResponse<>();
        response.setMessage("success");
        response.setResult(service.create(request));
        return response;
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ApiResponse<CollectionResponse> getById(@PathVariable UUID id) {
        ApiResponse<CollectionResponse> response = new ApiResponse<>();
        response.setMessage("success");
        response.setResult(service.getById(id));
        return response;
    }

    // GET ALL (pagination)
    @GetMapping
    public ApiResponse<Page<CollectionResponse>> getAll(Pageable pageable) {
        ApiResponse<Page<CollectionResponse>> response = new ApiResponse<>();
        response.setMessage("success");
        response.setResult(service.getAll(pageable));
        return response;
    }

    // UPDATE
    @PutMapping("/{id}")
    public ApiResponse<CollectionResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid CollectionRequest request
    ) {
        ApiResponse<CollectionResponse> response = new ApiResponse<>();
        response.setMessage("success");
        response.setResult(service.update(id, request));
        return response;
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable UUID id) {
        service.delete(id);

        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("success");
        response.setResult("Deleted successfully");
        return response;
    }
}