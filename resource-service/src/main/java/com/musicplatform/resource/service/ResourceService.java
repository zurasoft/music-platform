package com.musicplatform.resource.service;

import com.musicplatform.resource.client.SongServiceClient;
import com.musicplatform.resource.entity.Resource;
import com.musicplatform.resource.repository.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ResourceService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);

    private final ResourceRepository resourceRepository;
    private final MetadataService metadataService;
    private final SongServiceClient songServiceClient;

    @Autowired
    public ResourceService(ResourceRepository resourceRepository,
                           MetadataService metadataService,
                           SongServiceClient songServiceClient) {
        this.resourceRepository = resourceRepository;
        this.metadataService = metadataService;
        this.songServiceClient = songServiceClient;
    }

    public Long uploadResource(byte[] audioData) {
        logger.info("Starting MP3 upload process, file size: {} bytes", audioData.length);

        // Extract metadata using Tika
        Map<String, String> metadata = metadataService.extractMetadata(audioData);
        logger.info("Extracted metadata: {}", metadata);

        // Create resource entity
        Resource resource = new Resource(
                generateFilename(metadata),
                "audio/mpeg",
                (long) audioData.length,
                audioData
        );

        // Save to database
        Resource savedResource = resourceRepository.save(resource);
        logger.info("Saved resource with ID: {}", savedResource.getId());

        // Send metadata to Song Service
        songServiceClient.createSongMetadata(savedResource.getId(), metadata);

        return savedResource.getId();
    }

    public Optional<Resource> getResourceById(Long id) {
        return resourceRepository.findById(id);
    }

    public long[] deleteResources(long[] ids) {
        List<Long> deletedIds = new ArrayList<>();

        for (long id : ids) {
            if (resourceRepository.existsById(id)) {
                resourceRepository.deleteById(id);
                deletedIds.add(id);
                logger.info("Deleted resource with ID: {}", id);

                // Delete from Song Service (cascade requirement)
                songServiceClient.deleteSongMetadata(id);
            } else {
                logger.warn("Resource with ID {} not found for deletion", id);
            }
        }

        return deletedIds.stream().mapToLong(Long::longValue).toArray();
    }

    private String generateFilename(Map<String, String> metadata) {
        String artist = metadata.get("artist");
        String title = metadata.get("name");

        // Create filename like "Artist - Title.mp3"
        if (!"Unknown Artist".equals(artist) && !"Unknown Title".equals(title)) {
            return sanitizeFilename(artist + " - " + title + ".mp3");
        }

        return "uploaded-file.mp3";
    }

    private String sanitizeFilename(String filename) {
        // Remove invalid filename characters
        return filename.replaceAll("[^a-zA-Z0-9\\.\\-_ ]", "").trim();
    }
}
