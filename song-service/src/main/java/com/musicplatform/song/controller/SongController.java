package com.musicplatform.song.controller;

import com.musicplatform.song.dto.CreateSongRequest;
import com.musicplatform.song.dto.CreateSongResponse;
import com.musicplatform.song.dto.SongResponse;
import com.musicplatform.song.dto.DeleteSongResponse;
import com.musicplatform.song.service.SongService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/songs")
public class SongController {

    private static final Logger logger = LoggerFactory.getLogger(SongController.class);

    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    @PostMapping
    public ResponseEntity<CreateSongResponse> createSong(@Valid @RequestBody CreateSongRequest songRequest) {
        logger.info("Received request to create song metadata: {}", songRequest);

        Long songId = songService.createSong(songRequest);
        return ResponseEntity.ok(new CreateSongResponse(songId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongResponse> getSong(@PathVariable("id") Long id) {
        logger.info("Fetching song metadata for ID: {}", id);

        Optional<SongResponse> song = songService.getSongById(id);
        if (song.isPresent()) {
            return ResponseEntity.ok(song.get());
        } else {
            throw new IllegalArgumentException("Song metadata with ID=" + id + " not found");
        }
    }

    @DeleteMapping
    public ResponseEntity<DeleteSongResponse> deleteSongs(@RequestParam("id") String id) {
        try {
            logger.info("Deleting song metadata for IDs: {}", id);

            if (id.length() >= 200) {
                throw new IllegalArgumentException("CSV string exceeds 200 characters");
            }

            String[] idStrings = id.split(",");
            long[] ids = new long[idStrings.length];

            for (int i = 0; i < idStrings.length; i++) {
                String idStr = idStrings[i].trim();
                if (idStr.isEmpty()) {
                    throw new IllegalArgumentException("Empty ID in CSV string");
                }
                ids[i] = Long.parseLong(idStr);

                if (ids[i] <= 0) {
                    throw new IllegalArgumentException("ID must be positive: " + ids[i]);
                }
            }

            long[] deletedIds = songService.deleteSongs(ids);
            return ResponseEntity.ok(new DeleteSongResponse(deletedIds));

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format in CSV string: " + id);
        }
    }
}
