package com.esprit.pi.services;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileStorageConfig {

    @Bean
    public FileStorageProperties fileStorageProperties() {
        return new FileStorageProperties();
    }
}

