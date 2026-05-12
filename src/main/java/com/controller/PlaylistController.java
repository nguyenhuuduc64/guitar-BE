package com.controller;

import com.dto.request.ApiResponse;
import com.dto.request.PlaylistRequest;
import com.dto.response.PlaylistResponse;
import com.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public ApiResponse<PlaylistResponse> create(
            @RequestBody PlaylistRequest request
    ) {

        return ApiResponse.<PlaylistResponse>builder()
                .result(playlistService.create(request))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<PlaylistResponse> getById(
            @PathVariable UUID id
    ) {

        return ApiResponse.<PlaylistResponse>builder()
                .result(playlistService.getById(id))
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<PlaylistResponse>> getMyPlaylists(
            @PathVariable UUID userId
    ) {

        return ApiResponse.<List<PlaylistResponse>>builder()
                .result(playlistService.getMyPlaylists(userId))
                .build();
    }

    @PostMapping("/{playlistId}/chords/{chordId}")
    public ApiResponse<PlaylistResponse> addChord(
            @PathVariable UUID playlistId,
            @PathVariable UUID chordId
    ) {

        return ApiResponse.<PlaylistResponse>builder()
                .result(
                        playlistService.addChord(
                                playlistId,
                                chordId
                        )
                )
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable UUID id
    ) {

        playlistService.delete(id);

        return ApiResponse.<Void>builder()
                .message("Delete playlist success")
                .build();
    }
}