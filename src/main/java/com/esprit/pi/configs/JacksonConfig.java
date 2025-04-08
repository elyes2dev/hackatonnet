package com.esprit.pi.configs;

import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // Customize the max nesting depth
        objectMapper.getFactory().setStreamWriteConstraints(StreamWriteConstraints.builder()
                .maxNestingDepth(20000)  // Set it to a higher value as needed
                .build());

        return objectMapper;
    }
}

