package com.musicplatform.resource.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "audio_data", nullable = false)
    private byte[] audioData;

    public Resource() {
    }

    public Resource(byte[] audioData) {
        this.audioData = audioData;
    }

    public Long getId() {
        return id;
    }

    public byte[] getAudioData() {
        return audioData;
    }
}
