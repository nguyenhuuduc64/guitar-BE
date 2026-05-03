package com.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class CollectionResponse {
    private UUID id;
    private String name;
    private String slug;
    private LocalDateTime createdAt;
    private UUID userId;
    private Set<UUID> chordIds;
}
