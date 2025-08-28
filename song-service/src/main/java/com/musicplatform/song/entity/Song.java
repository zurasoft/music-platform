package com.musicplatform.song.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "songs")
public class Song {

    @Id
    private Long id; // Uses Resource ID (one-to-one relationship)

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "artist", nullable = false, length = 100)
    private String artist;

    @Column(name = "album", nullable = false, length = 100)
    private String album;

    /**
     * Duration of the song in "mm:ss" format (e.g. 03:45).
     */
    @Column(name = "duration", nullable = false, length = 5)
    private String duration;

    /**
     * Release year in "YYYY" format (e.g. 2024).
     */
    @Column(name = "year", nullable = false, length = 4)
    private String year;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Song() {
    }

    public Song(Long id, String name, String artist, String album, String duration, String year) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.year = year;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
