package com.service;

import com.dto.request.ChordRequest;
import com.dto.response.ChordResponse;
import com.entity.Artist;
import com.entity.Category;
import com.entity.Chord;
import com.entity.ChordView;
import com.exception.AppException;
import com.exception.ErrorCode;
import com.mapper.ChordMapper;
import com.repository.ArtistRepository;
import com.repository.CategoryRepository;
import com.repository.ChordRepository;
import com.repository.ChordViewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChordService {
    private final ArtistRepository artistRepository;
    private final ChordRepository chordRepository;
    private final CategoryRepository categoryRepository;
    private final ChordMapper chordMapper;
    private final ChordViewRepository chordViewRepository;
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

    public List<ChordResponse> getByArtistId(UUID artistId) {

        return chordRepository.findByArtistId(artistId)
                .stream().map(chordMapper::toResponse).toList();
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

    public List<ChordResponse> getTrendingThisWeek() {

        LocalDateTime fromDate =
                LocalDateTime.now().minusDays(7);

        List<UUID> chordIds =
                chordViewRepository
                        .findTrendingChordIds(fromDate);

        List<Chord> chords =
                chordRepository.findByIdIn(chordIds);

        return chords.stream()
                .map(chordMapper::toResponse)
                .toList();
    }

    @Transactional
    @Modifying
    @Query("UPDATE Chord c SET c.views = c.views + 1 WHERE c.id = :id")
    public void increaseView(
            UUID chordId,
            UUID userId
    ) {

        Chord chord = chordRepository.findById(chordId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.CHORD_NOT_FOUND)
                );

        Optional<ChordView> lastView;

        // USER ĐÃ LOGIN
        if (userId != null) {

            lastView =
                    chordViewRepository
                            .findTopByChord_IdAndUserIdOrderByViewedAtDesc(
                                    chordId,
                                    userId
                            );
        }




        // TĂNG VIEW
        Long currentViews =
                chord.getViews() == null
                        ? 0L
                        : chord.getViews();

        chord.setViews(currentViews + 1);

        chordRepository.save(chord);

        // LƯU LỊCH SỬ VIEW
        ChordView chordView = ChordView.builder()
                .chord(chord)
                .userId(userId)
                .viewedAt(LocalDateTime.now())
                .build();

        chordViewRepository.save(chordView);
    }
}