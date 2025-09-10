package com.musicplatform.resource.service;

import com.musicplatform.resource.client.SongServiceClient;
import com.musicplatform.resource.dto.CreateResourceResponse;
import com.musicplatform.resource.dto.DeleteResourceResponse;
import com.musicplatform.resource.entity.Resource;
import com.musicplatform.resource.exception.InvalidResourceException;
import com.musicplatform.resource.exception.DataProcessingException;
import com.musicplatform.resource.exception.ResourceNotFoundException;
import com.musicplatform.resource.repository.ResourceRepository;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
public class ResourceService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);
    private static final String AUDIO_MPEG_MEDIA_TYPE = "audio/mpeg";
    private static final int SECONDS_PER_MINUTE = 60;

    private final ResourceRepository resourceRepository;
    private final SongServiceClient songServiceClient;

    @Autowired
    public ResourceService(ResourceRepository resourceRepository,
                           SongServiceClient songServiceClient) {
        this.resourceRepository = resourceRepository;
        this.songServiceClient = songServiceClient;
    }

    public CreateResourceResponse create(byte[] audioData) {
        validateAudio(audioData);

        Long savedResourceId = resourceRepository.save(new Resource(audioData)).getId();
        logger.info("Created resource with ID: {}", savedResourceId);
        Map<String, String> songMetadata = extractSongMetadata(audioData);

        try {
            songServiceClient.saveSongMetadata(savedResourceId, songMetadata);
            return new CreateResourceResponse(savedResourceId);
        } catch (DataProcessingException dataProcessingException) {
            resourceRepository.deleteById(savedResourceId);
            logger.info("Deleted recently created resource with id: {}", savedResourceId);

            throw new DataProcessingException(
                    "Failed to save resource for the following reason: " + dataProcessingException.getMessage());
        }
    }

    public byte[] getById(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException(String.format("Invalid value '%s' for ID. Must be a positive integer", id));
        }

        return resourceRepository.findById(id)
                .map(Resource::getAudioData)
                .filter(data -> data.length > 0)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource with ID=" + id + " not found"));
    }

    public DeleteResourceResponse deleteAllByIds(String csvIds) {
        if (csvIds.length() >= 200) {
            throw new IllegalArgumentException(String.format("CSV string is too long: received %s characters, maximum allowed is 200", csvIds.length()));
        }

        String[] idStrings = csvIds.split(",");
        List<Long> deletedIds = new ArrayList<>();
        Long idForDeletion;

        for (String idString : idStrings) {
            String idStr = idString.trim();

            try {
                idForDeletion = Long.parseLong(idStr);

                if (resourceRepository.existsById(idForDeletion)) {
                    resourceRepository.deleteById(idForDeletion);
                    logger.info("Deleted resource with ID: {}", idForDeletion);

                    deletedIds.add(idForDeletion);
                } else {
                    logger.warn("Resource with ID {} not found for deletion", idForDeletion);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(String.format("Invalid ID format: '%s'. Only positive integers are allowed", idStr));
            }
        }

        songServiceClient.deleteAllSongMetadataByIds(csvIds);

        return new DeleteResourceResponse(deletedIds);
    }

    private boolean isAudioMpegMediaType(byte[] mp3Data) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mp3Data)) {
            String mediaType = new Tika().detect(byteArrayInputStream);
            return AUDIO_MPEG_MEDIA_TYPE.equalsIgnoreCase(mediaType);
        } catch (IOException e) {
            logger.error("Error while detecting MP3 data", e);
            return false;
        }
    }

    public Map<String, String> extractSongMetadata(byte[] audioData) {
        Map<String, String> metadataMap = new HashMap<>();

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(audioData)) {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext parseContext = new ParseContext();
            Mp3Parser mp3Parser = new Mp3Parser();
            mp3Parser.parse(inputStream, handler, metadata, parseContext);

            metadataMap.put("name", metadata.get("dc:title"));
            metadataMap.put("artist", metadata.get("xmpDM:artist"));
            metadataMap.put("album", metadata.get("xmpDM:album"));

            String durationStr = metadata.get("xmpDM:duration");
            metadataMap.put("duration", durationStr == null ? null
                    : formatDurationFromSecondsToMinutesAndSeconds(durationStr));

            metadataMap.put("year", metadata.get("xmpDM:releaseDate"));
            logger.info("Successfully extracted MP3 metadata: {}", metadataMap);

            return metadataMap;
        } catch (IOException | SAXException | TikaException e) {
            throw new DataProcessingException("Failed to extract MP3 metadata", e);
        }
    }

    private String formatDurationFromSecondsToMinutesAndSeconds(String durationStr) {
        try {
            double totalSecondsDouble = Double.parseDouble(durationStr);
            int totalSeconds = (int) totalSecondsDouble;
            int minutes = totalSeconds / SECONDS_PER_MINUTE;
            int seconds = totalSeconds % SECONDS_PER_MINUTE;
            return String.format("%02d:%02d", minutes, seconds);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void validateAudio(byte[] audioData) {
        Map<Supplier<Boolean>, String> checks = Map.of(
                () -> audioData == null || audioData.length == 0, "MP3 data is empty",
                () -> !isAudioMpegMediaType(audioData), "Invalid MP3 data. Expected audio/mpeg media type");

        checks.forEach((condition, message) -> {
            if (Boolean.TRUE.equals(condition.get())) {
                logger.warn("Validation failed: {}", message);
                throw new InvalidResourceException(message);
            }
        });
    }
}
