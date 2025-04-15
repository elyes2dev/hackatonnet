package com.esprit.pi.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ProductPriceService {

    @Value("${google.search.api.key}")
    private String apiKey;

    @Value("${google.search.engine.id}")
    private String searchEngineId;

    private final RestTemplate restTemplate;

    public Double fetchProductPrice(String productName) {
        try {
            System.out.println("API Key: " + apiKey);
            System.out.println("Search Engine ID: " + searchEngineId);

            String url = String.format(
                    "https://www.googleapis.com/customsearch/v1?q=%s&cx=%s&key=%s",
                    productName.replace(" ", "+"),
                    searchEngineId,
                    apiKey
            );

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            System.out.println("Google API Response: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return extractPriceFromResponse(response.getBody());
            }
        } catch (Exception e) {
            System.err.println("Error fetching product price: " + e.getMessage());
        }
        return null; // Return null if no price found
    }

    private Double extractPriceFromResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);

            if (root.has("items")) {
                for (JsonNode item : root.get("items")) {
                    String snippet = item.get("snippet").asText();
                    System.out.println("Snippet Found: " + snippet);

                    // Improved price pattern: Ensures the price has a currency symbol and valid format
                    Pattern pricePattern = Pattern.compile("(?i)(?:\\$|€|£)\\s?(\\d{1,5}(?:\\.\\d{1,2})?)");
                    Matcher matcher = pricePattern.matcher(snippet);

                    while (matcher.find()) {
                        double extractedPrice = Double.parseDouble(matcher.group(1));

                        // Filtering out unrealistic values (optional)
                        if (extractedPrice > 100 && extractedPrice < 10000) {
                            return extractedPrice;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
        }
        return null;
    }

}
