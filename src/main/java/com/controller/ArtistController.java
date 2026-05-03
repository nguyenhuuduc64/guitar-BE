package com.controller;

import com.dto.request.ApiResponse;
import com.dto.request.ArtistRequest;
import com.service.ArtistService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/artists")
@RequiredArgsConstructor
@Slf4j
public class ArtistController {

    private final ArtistService artistService;

    @PostMapping
    public ApiResponse<Object> create(@RequestBody ArtistRequest request) {

        return ApiResponse.builder()
                .result(artistService.create(request))
                .build();
    }

    @GetMapping("")
    public ApiResponse<Object> getAll() {
        return ApiResponse.builder()
                .result(artistService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<Object> getById(@PathVariable UUID id) {
        return ApiResponse.builder()
                .result(artistService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<Object> update(
            @PathVariable UUID id,
            @RequestBody ArtistRequest request
    ) {
        return ApiResponse.builder()
                .result(artistService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Object> delete(@PathVariable UUID id) {
        artistService.delete(id);

        return ApiResponse.builder()
                .result("Deleted successfully")
                .build();
    }
}