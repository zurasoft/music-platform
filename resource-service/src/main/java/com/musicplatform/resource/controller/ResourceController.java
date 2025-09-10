package com.musicplatform.resource.controller;

import com.musicplatform.resource.dto.CreateResourceResponse;
import com.musicplatform.resource.dto.DeleteResourceResponse;
import com.musicplatform.resource.service.ResourceService;
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
@RequestMapping("/resources")
public class ResourceController {

    private static final String AUDIO_MPEG_MEDIA_TYPE = "audio/mpeg";

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping(consumes = AUDIO_MPEG_MEDIA_TYPE)
    public ResponseEntity<CreateResourceResponse> create(@RequestBody byte[] audioData) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resourceService.create(audioData));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getById(@PathVariable("id") Long id) {
        byte[] audioData = resourceService.getById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(AUDIO_MPEG_MEDIA_TYPE))
                .contentLength(audioData.length)
                .body(audioData);
    }

    @DeleteMapping
    public ResponseEntity<DeleteResourceResponse> deleteAllByIds(@RequestParam("id") String csvIds) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(resourceService.deleteAllByIds(csvIds));
    }
}
