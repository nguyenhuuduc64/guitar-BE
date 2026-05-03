package com.repository;

import com.entity.Chord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChordRepository extends JpaRepository<Chord, UUID> {
    Optional<Chord> findBySlug(String slug);
    boolean existsBySlug(String slug);

}