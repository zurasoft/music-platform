package com.musicplatform.resource.service;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MetadataService {

    private static final Logger logger = LoggerFactory.getLogger(MetadataService.class);

    private static final String DEFAULT_TITLE = "Unknown Title";
    private static final String DEFAULT_ARTIST = "Unknown Artist";
    private static final String DEFAULT_ALBUM = "Unknown Album";
    private static final String DEFAULT_YEAR = "1500";
    private static final String DEFAULT_DURATION = "00:00";
    private static final int MIN_YEAR_LENGTH = 4;
    private static final String YEAR_REGEX = "\\d{4}.*";
    private static final int SECONDS_IN_MINUTE = 60;

    public Map<String, String> extractMetadata(byte[] mp3Data) {
        Map<String, String> metadataMap = new HashMap<>();

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(mp3Data)) {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext parseContext = new ParseContext();

            Mp3Parser mp3Parser = new Mp3Parser();
            mp3Parser.parse(inputStream, handler, metadata, parseContext);

            logExtractedMetadata(metadata);

            metadataMap.put("name", getMetadataValue(metadata, "title", "dc:title"));
            metadataMap.put("artist", getMetadataValue(metadata, "xmpDM:artist", "dc:creator", "artist"));
            metadataMap.put("album", getMetadataValue(metadata, "xmpDM:album", "album"));
            metadataMap.put("year", extractYear(metadata));
            metadataMap.put("duration", extractAndFormatDuration(metadata));

            setDefaultIfEmpty(metadataMap, "name", DEFAULT_TITLE);
            setDefaultIfEmpty(metadataMap, "artist", DEFAULT_ARTIST);
            setDefaultIfEmpty(metadataMap, "album", DEFAULT_ALBUM);
            setDefaultIfEmpty(metadataMap, "year", DEFAULT_YEAR);

        } catch (IOException | SAXException | TikaException e) {
            logger.error("Error extracting MP3 metadata: {}", e.getMessage());
            return createDefaultMetadata();
        }

        return metadataMap;
    }

    private void logExtractedMetadata(Metadata metadata) {
        if (logger.isDebugEnabled()) {
            logger.debug("=== MP3 Metadata ===");
            String[] metadataNames = metadata.names();
            for (String name : metadataNames) {
                logger.debug("{}: {}", name, metadata.get(name));
            }
        }
    }

    private String getMetadataValue(Metadata metadata, String... keys) {
        for (String key : keys) {
            String value = metadata.get(key);
            if (isValidMetadataValue(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private boolean isValidMetadataValue(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String extractYear(Metadata metadata) {
        String year = getMetadataValue(metadata, "xmpDM:releaseDate", "meta:creation-date", "date");
        if (year != null && year.length() >= MIN_YEAR_LENGTH && year.matches(YEAR_REGEX)) {
            return year.substring(0, MIN_YEAR_LENGTH);
        }
        return DEFAULT_YEAR;
    }

    private String extractAndFormatDuration(Metadata metadata) {
        String duration = getMetadataValue(metadata, "xmpDM:duration");
        return duration != null ? formatDurationFromSeconds(duration) : DEFAULT_DURATION;
    }

    private String formatDurationFromSeconds(String durationStr) {
        try {
            double seconds = Double.parseDouble(durationStr);
            int totalSeconds = (int) seconds;
            int minutes = totalSeconds / SECONDS_IN_MINUTE;
            int remainingSeconds = totalSeconds % SECONDS_IN_MINUTE;
            return String.format("%02d:%02d", minutes, remainingSeconds);
        } catch (NumberFormatException e) {
            logger.warn("Invalid duration format: {}", durationStr);
            return DEFAULT_DURATION;
        }
    }

    private void setDefaultIfEmpty(Map<String, String> map, String key, String defaultValue) {
        if (!isValidMetadataValue(map.get(key))) {
            map.put(key, defaultValue);
        }
    }

    private Map<String, String> createDefaultMetadata() {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("name", DEFAULT_TITLE);
        defaults.put("artist", DEFAULT_ARTIST);
        defaults.put("album", DEFAULT_ALBUM);
        defaults.put("year", DEFAULT_YEAR);
        defaults.put("duration", DEFAULT_DURATION);
        return defaults;
    }
}
