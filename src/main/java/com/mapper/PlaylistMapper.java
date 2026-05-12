package com.mapper;

import com.dto.request.PlaylistRequest;
import com.dto.response.PlaylistResponse;
import com.entity.Chord;
import com.entity.Playlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chords", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Playlist toEntity(PlaylistRequest request);

    default PlaylistResponse toResponse(Playlist playlist) {

        Set<PlaylistResponse.ChordItem> chordItems =
                playlist.getChords()
                        .stream()
                        .map(this::toChordItem)
                        .collect(Collectors.toSet());

        return PlaylistResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .userId(playlist.getUserId())
                .createdAt(playlist.getCreatedAt())
                .chords(chordItems)
                .build();
    }

    default PlaylistResponse.ChordItem toChordItem(Chord chord) {
        return PlaylistResponse.ChordItem.builder()
                .id(chord.getId())
                .title(chord.getTitle())
                .slug(chord.getSlug())
                .build();
    }
}