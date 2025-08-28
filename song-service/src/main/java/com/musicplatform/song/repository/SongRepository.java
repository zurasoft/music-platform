package com.musicplatform.song.repository;

import com.musicplatform.song.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {

    // Additional query methods can be added here if needed
    boolean existsByNameAndArtist(String name, String artist);
}