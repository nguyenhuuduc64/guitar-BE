package com.dto.request;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CollectionRequest {
    private String name;
    private UUID userId;
    private Set<UUID> chordIds;
}
