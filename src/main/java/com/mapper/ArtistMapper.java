package com.mapper;

import com.dto.request.ArtistRequest;
import com.dto.response.ArtistResponse;
import com.entity.Artist;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ArtistMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    Artist toEntity(ArtistRequest request);

    ArtistResponse toResponse(Artist artist);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "slug", ignore = true)
    void update(@MappingTarget Artist artist, ArtistRequest request);
}