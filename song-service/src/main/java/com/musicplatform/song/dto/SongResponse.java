package com.musicplatform.song.dto;

public record SongResponse(
        Long id,
        String name,
        String artist,
        String album,
        String duration,
        String year
) {}
