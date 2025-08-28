package com.musicplatform.resource.controller;

import com.musicplatform.resource.dto.CreateResourceResponse;
import com.musicplatform.resource.dto.DeleteResourceResponse;
import com.musicplatform.resource.entity.Resource;
import com.musicplatform.resource.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping(consumes = "audio/mpeg")
    public ResponseEntity<CreateResourceResponse> uploadResource(@RequestBody byte[] audioData) {
        if (!isValidMP3(audioData)) {
            throw new IllegalArgumentException("The request body is invalid MP3");
        }

        Long resourceId = resourceService.uploadResource(audioData);
        return ResponseEntity.ok(new CreateResourceResponse(resourceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getResource(@PathVariable("id") Long id) {
        Optional<Resource> resource = resourceService.getResourceById(id);

        if (resource.isPresent()) {
            return ResponseEntity.ok()
                    .header("Content-Type", "audio/mpeg")
                    .body(resource.get().getAudioData());
        } else {
            throw new IllegalArgumentException("Resource with ID=" + id + " not found");
        }
    }

    @DeleteMapping
    public ResponseEntity<DeleteResourceResponse> deleteResources(@RequestParam("id") String id) {
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

            long[] deletedIds = resourceService.deleteResources(ids);
            return ResponseEntity.ok(new DeleteResourceResponse(deletedIds));

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format in CSV string: " + id);
        }
    }
}
