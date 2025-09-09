package com.musicplatform.song.service;

import com.musicplatform.song.dto.CreateSongRequest;
import com.musicplatform.song.dto.CreateSongResponse;
import com.musicplatform.song.dto.DeleteSongResponse;
import com.musicplatform.song.dto.SongResponse;
import com.musicplatform.song.entity.Song;
import com.musicplatform.song.exception.ResourceNotFoundException;
import com.musicplatform.song.repository.SongRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SongService {

    private static final Logger logger = LoggerFactory.getLogger(SongService.class);

    private final SongRepository songRepository;

    @Autowired
    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public CreateSongResponse create(CreateSongRequest createSongRequest) {
        if (songRepository.existsById(createSongRequest.id())) {
            throw new IllegalArgumentException("Metadata for ID=" + createSongRequest.id() + " already exists");
        }

        Song song = toEntity(createSongRequest);

        Song savedSong = songRepository.save(song);
        logger.info("Created song metadata for ID: {}", savedSong.getId());

        return new CreateSongResponse(savedSong.getId());
    }

    public SongResponse getById(Long id) {
        return songRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Song metadata with ID=" + id + " not found"));
    }

    public DeleteSongResponse deleteAllByIds(String csvIds) {
        if (csvIds.length() >= 200) {
            throw new IllegalArgumentException("CSV string exceeds 200 characters");
        }

        String[] idStrings = csvIds.split(",");
        List<Long> deletedIds = new ArrayList<>();
        Long idForDeletion;

        for (String idString : idStrings) {
            String idStr = idString.trim();

            try {
                idForDeletion = Long.parseLong(idStr);

                if (songRepository.existsById(idForDeletion)) {
                    songRepository.deleteById(idForDeletion);
                    logger.info("Deleted song metadata with ID: {}", idForDeletion);

                    deletedIds.add(idForDeletion);
                } else {
                    logger.warn("Song metadata with ID {} not found for deletion", idForDeletion);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid ID format in CSV string: " + idStr);
            }
        }

        return new DeleteSongResponse(deletedIds);
    }

    private SongResponse toDto(Song song) {
        return new SongResponse(
                song.getId(),
                song.getName(),
                song.getArtist(),
                song.getAlbum(),
                song.getDuration(),
                song.getYear()
        );
    }

    private Song toEntity(CreateSongRequest createSongRequest) {
        return new Song(
                createSongRequest.id(),
                createSongRequest.name(),
                createSongRequest.artist(),
                createSongRequest.album(),
                createSongRequest.duration(),
                createSongRequest.year());
    }
}
