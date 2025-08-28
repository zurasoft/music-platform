package com.musicplatform.song.dto;

import jakarta.validation.constraints.*;

public record CreateSongRequest(
        @NotNull(message = "ID is required")
        @Positive(message = "ID must be positive")
        Long id,

        @NotBlank(message = "Name is required")
        @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
        String name,

        @NotBlank(message = "Artist is required")
        @Size(min = 1, max = 100, message = "Artist must be between 1 and 100 characters")
        String artist,

        @NotBlank(message = "Album is required")
        @Size(min = 1, max = 100, message = "Album must be between 1 and 100 characters")
        String album,

        @NotBlank(message = "Duration is required")
        @Pattern(regexp = "\\d{2}:\\d{2}", message = "Duration must be in mm:ss format with leading zeros")
        String duration,

        @NotBlank(message = "Year is required")
        @Pattern(regexp = "\\d{4}", message = "Year must be in YYYY format")
        @Min(value = 1900, message = "Year must be between 1900 and 2099")
        @Max(value = 2099, message = "Year must be between 1900 and 2099")
        String year
) { }

