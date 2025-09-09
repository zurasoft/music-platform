package com.musicplatform.resource.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class SongServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(SongServiceClient.class);
    private static final String SONGS_ENDPOINT = "/songs";
    private static final String ID_QUERY_PARAM = "?id=";

    private final RestClient restClient;

    @Autowired
    public SongServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${song-service.url}") String songServiceBaseUrl) {
        this.restClient = restClientBuilder
                .baseUrl(songServiceBaseUrl)
                .build();
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
                .uri(SONGS_ENDPOINT)
                .contentType(APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toBodilessEntity();

//                .exchange();

//        String result = restClient.get()
//                .uri("https://example.com/this-url-does-not-exist")
//                .retrieve()
//                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
//                    throw new MyCustomRuntimeException(response.getStatusCode(), response.getHeaders());
//                })
//                .body(String.class);


//        logger.info("Successfully created song metadata for resource ID: {}", resourceId);
//        logger.warn("Failed to create song metadata. Status: {}", response.getStatusCode());
    }

    public void deleteAllSongMetadataByIds(String csvIds) {
        restClient.delete()
                .uri(SONGS_ENDPOINT + ID_QUERY_PARAM + csvIds)
                .retrieve()
                .toBodilessEntity();
    }
}
