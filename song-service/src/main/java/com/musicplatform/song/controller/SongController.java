package com.musicplatform.song.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/songs")
public class SongController {

    private static final Logger logger = LoggerFactory.getLogger(SongController.class);

    @PostMapping
    public ResponseEntity<Map<String, Long>> createSong(@RequestBody Map<String, Object> songData) {
        logger.info("Received song metadata: {}", songData);

        // TODO: Validate and save to database
        Long resourceId = ((Number) songData.get("id")).longValue();

        return ResponseEntity.ok(Map.of("id", resourceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSong(@PathVariable("id") Long id) {
        logger.info("Fetching song metadata for ID: {}", id);

        // TODO: Implement actual retrieval
        return ResponseEntity.ok(Map.of(
                "id", id,
                "name", "Test Song",
                "artist", "Test Artist",
                "album", "Test Album",
                "duration", "03:45",
                "year", "2024"
        ));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, long[]>> deleteSongs(@RequestParam("id") String id) {
        logger.info("Deleting song metadata for IDs: {}", id);

        // TODO: Implement actual deletion
        return ResponseEntity.ok(Map.of("ids", new long[]{1L, 2L}));
    }
}
