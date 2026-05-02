package com.mapper;

import com.dto.request.ChordRequest;
import com.dto.response.ChordResponse;
import com.entity.Chord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
@Mapper(componentModel = "spring")
public interface ChordMapper {

    @Mapping(target = "category", ignore = true)
    Chord toEntity(ChordRequest request);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ChordResponse toResponse(Chord chord);

    @Mapping(target = "category", ignore = true)
    void updateEntity(@MappingTarget Chord chord, ChordRequest request);
}