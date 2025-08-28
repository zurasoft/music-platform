package com.musicplatform.song.service;

import com.musicplatform.song.dto.CreateSongRequest;
import com.musicplatform.song.dto.SongResponse;
import com.musicplatform.song.entity.Song;
import com.musicplatform.song.repository.SongRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SongService {

    private static final Logger logger = LoggerFactory.getLogger(SongService.class);

    private final SongRepository songRepository;

    @Autowired
    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public Long createSong(CreateSongRequest songRequest) {
        if (songRepository.existsById(songRequest.id())) {
            throw new IllegalArgumentException("Metadata for ID=" + songRequest.id() + " already exists");
        }

        Song song = new Song(
                songRequest.id(),
                songRequest.name(),
                songRequest.artist(),
                songRequest.album(),
                songRequest.duration(),
                songRequest.year()
        );

        Song savedSong = songRepository.save(song);
        logger.info("Created song metadata for ID: {}", savedSong.getId());

        return savedSong.getId();
    }

    public Optional<SongResponse> getSongById(Long id) {
        return songRepository.findById(id)
                .map(this::convertToResponse);
    }

    public long[] deleteSongs(long[] ids) {
        List<Long> deletedIds = new ArrayList<>();

        for (long id : ids) {
            if (songRepository.existsById(id)) {
                songRepository.deleteById(id);
                deletedIds.add(id);
                logger.info("Deleted song metadata for ID: {}", id);
            } else {
                logger.warn("Song metadata with ID {} not found for deletion", id);
            }
        }

        return deletedIds.stream().mapToLong(Long::longValue).toArray();
    }

    private SongResponse convertToResponse(Song song) {
        return new SongResponse(
                song.getId(),
                song.getName(),
                song.getArtist(),
                song.getAlbum(),
                song.getDuration(),
                song.getYear()
        );
    }
}
