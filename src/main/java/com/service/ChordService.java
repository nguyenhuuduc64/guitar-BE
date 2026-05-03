package com.service;

import com.dto.request.ChordRequest;
import com.dto.response.ChordResponse;
import com.entity.Artist;
import com.entity.Category;
import com.entity.Chord;
import com.mapper.ChordMapper;
import com.repository.ArtistRepository;
import com.repository.CategoryRepository;
import com.repository.ChordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChordService {
    private final ArtistRepository artistRepository;
    private final ChordRepository chordRepository;
    private final CategoryRepository categoryRepository;
    private final ChordMapper chordMapper;

    public ChordResponse create(ChordRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Artist artist = artistRepository.findById(request.getArtistId())
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        Chord chord = Chord.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .slug(generateSlug(request.getTitle()))
                .category(category)
                .artist(artist)
                .build();

        chordRepository.save(chord);

        return chordMapper.toResponse(chord);
    }

    public List<ChordResponse> getAll() {
        return chordRepository.findAll()
                .stream()
                .map(chordMapper::toResponse)
                .toList();
    }

    public ChordResponse getById(UUID id) {
        Chord chord = chordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chord not found"));

        return chordMapper.toResponse(chord);
    }

    public ChordResponse update(UUID id, ChordRequest request) {

        Chord chord = chordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chord not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        chordMapper.updateEntity(chord, request);

        chord.setCategory(category);
        chord.setSlug(generateSlug(request.getTitle()));

        return chordMapper.toResponse(chordRepository.save(chord));
    }

    public void delete(UUID id) {
        chordRepository.deleteById(id);
    }

    private String generateSlug(String title) {
        String baseSlug = Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");

        String slug = baseSlug;
        int count = 1;

        while (chordRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + count;
            count++;
        }

        return slug;
    }
}