package com.musicplatform.song;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SongServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SongServiceApplication.class, args);
    }
}
