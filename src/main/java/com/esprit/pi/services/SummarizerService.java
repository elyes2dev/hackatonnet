package com.esprit.pi.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

@Service
public class SummarizerService {

    private final RestTemplate restTemplate = new RestTemplate();


    public String analyzeText(String text) {
        String url = "http://localhost:8000/analyze";

        Map<String, String> request = new HashMap<>();
        request.put("text", text);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        // Extract summary and label from response JSON
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            String summary = root.get("summary").asText();
            String label = root.get("label").asText();
            return "{\"Summary\": \"" + summary + "\"\n,\"Classification\": \"" + label+"\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing response";
        }
    }

}
