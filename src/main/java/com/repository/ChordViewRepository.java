package com.repository;

import com.entity.ChordView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChordViewRepository
        extends JpaRepository<ChordView, UUID> {

    Optional<ChordView>
    findTopByChord_IdAndUserIdOrderByViewedAtDesc(
            UUID chordId,
            UUID userId
    );
    // USER KHÁCH
    Optional<ChordView>
    findTopByChord_IdAndIpAddressOrderByViewedAtDesc(
            UUID chordId,
            String ipAddress
    );
    @Query("""
        SELECT cv.chord.id
        FROM ChordView cv
        WHERE cv.viewedAt >= :fromDate
        GROUP BY cv.chord.id
        ORDER BY COUNT(cv.id) DESC
    """)
    List<UUID> findTrendingChordIds(
            LocalDateTime fromDate
    );
}