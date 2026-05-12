package com.dto.request;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class PlaylistRequest {

    private String name;

    private String description;

    private UUID userId;

    private Set<UUID> chordIds;
}
