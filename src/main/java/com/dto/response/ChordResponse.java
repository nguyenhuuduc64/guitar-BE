package com.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChordResponse {
    UUID id;
    String title;
    String slug;
    String content;

    UUID categoryId;
    String categoryName;
    UUID artistId;
    Long views;
}