package com.musicplatform.resource.controller;

import com.musicplatform.resource.dto.ResourceResponseDto;
import com.musicplatform.resource.dto.DeleteResponseDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    @PostMapping
    public ResponseEntity<ResourceResponseDto> uploadResource(@RequestBody byte[] audioData) {
        // TODO: Implement actual MP3 upload logic
        return ResponseEntity.ok(new ResourceResponseDto(1L));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getResource(@PathVariable("id") Long id) {
        // TODO: Implement actual resource retrieval
        // Return fake MP3 data for now
        byte[] fakeAudioData = "fake-mp3-content".getBytes();
        return ResponseEntity.ok()
                .header("Content-Type", "audio/mpeg")
                .body(fakeAudioData);
    }

    @DeleteMapping
    public ResponseEntity<DeleteResponseDto> deleteResources(@RequestParam("id") String id) {
        try {
            // Validate CSV length (requirement: < 200 characters)
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

                // Validate ID is positive
                if (ids[i] <= 0) {
                    throw new IllegalArgumentException("ID must be positive: " + ids[i]);
                }
            }

            // TODO: Implement actual resource deletion
            return ResponseEntity.ok(new DeleteResponseDto(ids));

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format in CSV string: " + id);
        }
    }
}
