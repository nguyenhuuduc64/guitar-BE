package com.service;

import com.dto.request.ArtistRequest;
import com.dto.response.ArtistResponse;
import com.entity.Artist;
import com.mapper.ArtistMapper;
import com.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    public ArtistResponse create(ArtistRequest request) {
        Artist artist = artistMapper.toEntity(request);

        if (artistRepository.existsBySlug(artist.getSlug())) {
            throw new RuntimeException("Slug đã tồn tại");
        }

        return artistMapper.toResponse(artistRepository.save(artist));
    }

    public List<ArtistResponse> getAll() {
        return artistRepository.findAll()
                .stream()
                .map(artistMapper::toResponse)
                .toList();
    }

    public ArtistResponse getById(UUID id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy artist"));

        return artistMapper.toResponse(artist);
    }

    public ArtistResponse update(UUID id, ArtistRequest request) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy artist"));

        artistMapper.update(artist, request);

        return artistMapper.toResponse(artistRepository.save(artist));
    }

    public void delete(UUID id) {
        if (!artistRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy artist");
        }
        artistRepository.deleteById(id);
    }
}