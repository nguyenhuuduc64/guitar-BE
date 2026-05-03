package com.service;

import com.dto.request.CollectionRequest;
import com.dto.response.CollectionResponse;
import com.entity.Chord;
import com.entity.Collection;
import com.entity.User;
import com.mapper.CollectionMapper;
import com.repository.ChordRepository;
import com.repository.CollectionRepository;
import com.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final ChordRepository chordRepository;
    private final CollectionMapper mapper;

    // CREATE
    public CollectionResponse create(CollectionRequest request) {

        Collection collection = mapper.toEntity(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Chord> chords = new HashSet<>(
                chordRepository.findAllById(request.getChordIds())
        );

        collection.setUser(user);
        collection.setChords(chords);

        return mapper.toResponse(collectionRepository.save(collection));
    }

    // GET BY ID
    public CollectionResponse getById(UUID id) {

        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        return mapper.toResponse(collection);
    }

    // GET ALL
    public Page<CollectionResponse> getAll(Pageable pageable) {

        return collectionRepository.findAll(pageable)
                .map(mapper::toResponse);
    }

    // UPDATE
    public CollectionResponse update(UUID id, CollectionRequest request) {

        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        collection.setName(request.getName());
        collection.setSlug(mapper.toSlug(request.getName()));

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            collection.setUser(user);
        }

        if (request.getChordIds() != null) {
            Set<Chord> chords = new HashSet<>(
                    chordRepository.findAllById(request.getChordIds())
            );
            collection.setChords(chords);
        }

        return mapper.toResponse(collectionRepository.save(collection));
    }

    // DELETE
    public void delete(UUID id) {

        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        collectionRepository.delete(collection);
    }
}