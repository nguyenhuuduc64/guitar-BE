package com.controller;

import com.dto.request.ApiResponse;
import com.dto.request.ChordRequest;
import com.dto.response.ChordResponse;
import com.entity.Chord;
import com.service.ChordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chords")
@RequiredArgsConstructor
public class ChordController {

    private final ChordService chordService;

    @PostMapping
    public ApiResponse<ChordResponse> create(@RequestBody ChordRequest request) {
        return ApiResponse.<ChordResponse>builder()
                .result(chordService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ChordResponse>> getAll() {
        return ApiResponse.<List<ChordResponse>>builder()
                .result(chordService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ChordResponse> getById(@PathVariable UUID id) {
        return ApiResponse.<ChordResponse>builder()
                .result(chordService.getById(id))
                .build();
    }
    @PostMapping("/{chordId}/view")
    public ApiResponse<Void> increaseView(
            @PathVariable UUID chordId,
            UUID userId
    ) {

        chordService.increaseView(chordId, userId);

        return ApiResponse.<Void>builder()
                .message("Increase view success")
                .build();
    }
    @GetMapping("/artist/{id}")
    public ApiResponse<List<ChordResponse>> getChordsByArtist(@PathVariable UUID id) {
        return ApiResponse.<List<ChordResponse>>builder()
                .result(chordService.getByArtistId(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ChordResponse> update(@PathVariable UUID id,
                                             @RequestBody ChordRequest request) {
        return ApiResponse.<ChordResponse>builder()
                .result(chordService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        chordService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Deleted successfully")
                .build();
    }

    @GetMapping("/trending")
    public ApiResponse<List<ChordResponse>>
    getTrendingThisWeek() {

        return ApiResponse
                .<List<ChordResponse>>builder()
                .result(
                        chordService.getTrendingThisWeek()
                )
                .build();
    }
}