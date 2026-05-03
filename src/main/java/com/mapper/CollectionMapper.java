package com.mapper;

import com.dto.request.CollectionRequest;
import com.dto.response.CollectionResponse;
import com.entity.Chord;
import com.entity.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CollectionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", expression = "java(toSlug(request.getName()))")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "chords", ignore = true)
    Collection toEntity(CollectionRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "chordIds", expression = "java(mapChordIds(entity.getChords()))")
    CollectionResponse toResponse(Collection entity);

    default String toSlug(String input) {
        return input.toLowerCase().replace(" ", "-");
    }

    default Set<UUID> mapChordIds(Set<Chord> chords) {
        return chords.stream().map(Chord::getId).collect(Collectors.toSet());
    }
}
