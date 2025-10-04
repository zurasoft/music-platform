package com.musicplatform.resource.client;

import com.musicplatform.resource.exception.DataProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class SongServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(SongServiceClient.class);
    private static final String SONGS_ENDPOINT = "/songs";
    private static final String ID_QUERY_PARAM = "?id=";
    private static final String SONG_SERVICE_NAME = "song-service";

    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;

    @Autowired
    public SongServiceClient(
            DiscoveryClient discoveryClient,
            RestClient.Builder restClientBuilder) {

        this.discoveryClient = discoveryClient;
        this.restClient = restClientBuilder.build();
    }

    public void saveSongMetadata(Long resourceId, Map<String, String> metadata) {
        Map<String, Object> requestBody = Map.of(
                "id", resourceId,
                "name", metadata.get("name"),
                "artist", metadata.get("artist"),
                "album", metadata.get("album"),
                "duration", metadata.get("duration"),
                "year", metadata.get("year"));

        restClient.post()
                .uri(getServiceUrl() + SONGS_ENDPOINT)
                .contentType(APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .onStatus(this::isFailedResponse, (request, response) -> {
                    throw new DataProcessingException("Failed to save appropriate metadata for resource with ID: " + resourceId);
                })
                .toBodilessEntity();

        logger.info("Successfully saved metadata for resource ID: {}", resourceId);
    }

    public void deleteAllSongMetadataByIds(String csvIds) {
        restClient.delete()
                .uri(getServiceUrl() + SONGS_ENDPOINT + ID_QUERY_PARAM + csvIds)
                .retrieve()
                .toBodilessEntity();
    }

    private String getServiceUrl() {
        ServiceInstance instance = discoveryClient.getInstances(SONG_SERVICE_NAME)
                .stream()
                .findFirst()
                .orElseThrow(() -> new DataProcessingException("No instances of '" + SONG_SERVICE_NAME + "' available"));

        return instance.getUri().toString();
    }

    private boolean isFailedResponse(HttpStatusCode status) {
        return !status.is2xxSuccessful();
    }
}
