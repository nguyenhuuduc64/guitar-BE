package com.service;

import com.dto.request.PlaylistRequest;
import com.dto.response.PlaylistResponse;
import com.entity.Chord;
import com.entity.Playlist;
import com.exception.AppException;
import com.exception.ErrorCode;
import com.mapper.PlaylistMapper;
import com.repository.ChordRepository;
import com.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final ChordRepository chordRepository;
    private final PlaylistMapper playlistMapper;

    public PlaylistResponse create(PlaylistRequest request) {

        Playlist playlist = playlistMapper.toEntity(request);

        if (request.getChordIds() != null) {

            List<Chord> chords =
                    chordRepository.findAllById(request.getChordIds());

            playlist.setChords(new HashSet<>(chords));
        }

        return playlistMapper.toResponse(
                playlistRepository.save(playlist)
        );
    }

    public List<PlaylistResponse> getMyPlaylists(UUID userId) {

        return playlistRepository.findByUserId(userId)
                .stream()
                .map(playlistMapper::toResponse)
                .collect(Collectors.toList());
    }

    public PlaylistResponse getById(UUID id) {

        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() ->
                        new AppException(ErrorCode.PLAYLIST_NOT_FOUND)
                );

        return playlistMapper.toResponse(playlist);
    }

    public PlaylistResponse addChord(
            UUID playlistId,
            UUID chordId
    ) {

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.PLAYLIST_NOT_FOUND)
                );

        Chord chord = chordRepository.findById(chordId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.CHORD_NOT_FOUND)
                );

        playlist.getChords().add(chord);

        return playlistMapper.toResponse(
                playlistRepository.save(playlist)
        );
    }

    public void delete(UUID id) {

        playlistRepository.deleteById(id);
    }
}