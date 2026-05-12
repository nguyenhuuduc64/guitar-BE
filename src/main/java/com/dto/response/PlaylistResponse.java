package com.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistResponse {

    private UUID id;

    private String name;

    private String description;

    private UUID userId;

    private LocalDateTime createdAt;

    private Set<ChordItem> chords;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChordItem {

        private UUID id;

        private String title;

        private String slug;
    }
}