package com.musicplatform.song.controller;

import com.musicplatform.song.dto.CreateSongRequest;
import com.musicplatform.song.dto.CreateSongResponse;
import com.musicplatform.song.dto.SongResponse;
import com.musicplatform.song.dto.DeleteSongResponse;
import com.musicplatform.song.service.SongService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/songs")
public class SongController {

    private static final MediaType APPLICATION_JSON_MEDIA_TYPE = MediaType.APPLICATION_JSON;

    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    @PostMapping
    public ResponseEntity<CreateSongResponse> create(@Valid @RequestBody CreateSongRequest createSongRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(songService.create(createSongRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(APPLICATION_JSON_MEDIA_TYPE)
                .body(songService.getById(id));
    }

    @DeleteMapping
    public ResponseEntity<DeleteSongResponse> deleteAllByIds(@RequestParam("id") String csvIds) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(songService.deleteAllByIds(csvIds));
    }
}
