package com.musicplatform.resource.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SongServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(SongServiceClient.class);

    private final RestTemplate restTemplate;
    private final String songServiceUrl;

    public SongServiceClient(RestTemplate restTemplate, @Value("${song-service.url}") String songServiceUrl) {
        this.restTemplate = restTemplate;
        this.songServiceUrl = songServiceUrl;
    }

    public void createSongMetadata(Long resourceId, Map<String, String> metadata) {
        try {
            String url = songServiceUrl + "/songs";
            logger.info("Attempting to call Song Service at: {}", url);

            // Create request body matching Song Service API
            Map<String, Object> requestBody = Map.of(
                    "id", resourceId,
                    "name", metadata.get("name"),
                    "artist", metadata.get("artist"),
                    "album", metadata.get("album"),
                    "duration", metadata.get("duration"),
                    "year", metadata.get("year")
            );

            logger.info("Request body: {}", requestBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully created song metadata for resource ID: {}", resourceId);
            } else {
                logger.warn("Failed to create song metadata. Status: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("Error communicating with Song Service for resource ID {}: {}", resourceId, e.getMessage(), e);
            // Don't throw exception - Resource Service should continue even if Song Service fails
        }
    }

    public void deleteSongMetadata(Long resourceId) {
        try {
            String url = songServiceUrl + "/songs?id=" + resourceId;

            restTemplate.delete(url);
            logger.info("Successfully deleted song metadata for resource ID: {}", resourceId);

        } catch (Exception e) {
            logger.error("Error deleting song metadata for resource ID {}: {}", resourceId, e.getMessage());
            // Don't throw exception - allow resource deletion to continue
        }
    }
}
