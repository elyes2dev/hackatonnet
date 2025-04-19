package com.esprit.pi.Service;

import com.esprit.pi.DTO.VerificationResultDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.Normalizer;
import java.util.*;

@Service
public class CompanyVerifier {

    private static final String API_URL = "https://api.opencorporates.com/v0.4/companies/search?q=%s&api_token=%s";
    private static final String API_TOKEN = "LWmYvKPS7VZ1oW3m3xHe";

    // Map common country names to their likely country codes
    private static final Map<String, String> JURISDICTION_MAPPINGS = new HashMap<>();
    static {
        JURISDICTION_MAPPINGS.put("tunisia", "tn");
        JURISDICTION_MAPPINGS.put("united states", "us");
        JURISDICTION_MAPPINGS.put("usa", "us");
        JURISDICTION_MAPPINGS.put("united kingdom", "gb");
        JURISDICTION_MAPPINGS.put("uk", "gb");
        // Add more as needed
    }

    public VerificationResultDTO verifyCompanyFromOCR(Map<String, String> extractedFields) {
        String ocrName = extractedFields.getOrDefault("company_name", "");
        String ocrRegNumber = extractedFields.getOrDefault("registration_number", "");
        String ocrJurisdiction = extractedFields.getOrDefault("jurisdiction", "");
        String ocrCompanyType = extractedFields.getOrDefault("company_type", "");
        String ocrAddress = extractedFields.getOrDefault("registered_address", "");

        // Log received fields for debugging
        System.out.println("🔍 OCR Extracted Fields:");
        System.out.println("- Company Name: '" + ocrName + "'");
        System.out.println("- Registration Number: '" + ocrRegNumber + "'");
        System.out.println("- Jurisdiction: '" + ocrJurisdiction + "'");
        System.out.println("- Company Type: '" + ocrCompanyType + "'");
        System.out.println("- Address: '" + ocrAddress + "'");

        // Changed search order - try company name first, then registration number
        String searchQuery;
        if (!ocrName.isEmpty()) {
            searchQuery = ocrName;
            System.out.println("🔎 Searching by company name: " + searchQuery);
        } else if (!ocrRegNumber.isEmpty()) {
            searchQuery = ocrRegNumber;
            System.out.println("🔎 Searching by registration number: " + searchQuery);
        } else {
            return VerificationResultDTO.builder()
                    .success(false)
                    .message("❌ No company name or registration number available for search.")
                    .build();
        }

        String url = String.format(API_URL, searchQuery, API_TOKEN);
        RestTemplate restTemplate = new RestTemplate();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(response);

            // Check if results were found
            if (!json.has("results") || !json.getJSONObject("results").has("companies")) {
                System.out.println("❌ No results found in API response");

                // If we searched by name and got no results, try registration number as fallback
                if (searchQuery.equals(ocrName) && !ocrRegNumber.isEmpty()) {
                    System.out.println("🔄 No results by company name, trying registration number: " + ocrRegNumber);
                    url = String.format(API_URL, ocrRegNumber, API_TOKEN);
                    try {
                        response = restTemplate.getForObject(url, String.class);
                        json = new JSONObject(response);

                        if (!json.has("results") || !json.getJSONObject("results").has("companies")) {
                            System.out.println("❌ No results found by registration number either");
                            return VerificationResultDTO.builder()
                                    .success(false)
                                    .message("❌ No matching companies found in OpenCorporates.")
                                    .build();
                        }
                    } catch (Exception e2) {
                        System.out.println("❌ Error in fallback search: " + e2.getMessage());
                        return VerificationResultDTO.builder()
                                .success(false)
                                .message("❌ No matching companies found in OpenCorporates.")
                                .build();
                    }
                } else {
                    return VerificationResultDTO.builder()
                            .success(false)
                            .message("❌ No matching companies found in OpenCorporates.")
                            .build();
                }
            }

            JSONArray companies = json.getJSONObject("results").getJSONArray("companies");
            System.out.println("🔍 Found " + companies.length() + " companies from OpenCorporates:");

            for (int i = 0; i < companies.length(); i++) {
                JSONObject companyObj = companies.getJSONObject(i).getJSONObject("company");
                System.out.println("- " + companyObj.optString("name") + " (" + companyObj.optString("company_number") + ")");
            }

            for (int i = 0; i < companies.length(); i++) {
                JSONObject company = companies.getJSONObject(i).getJSONObject("company");

                String apiName = company.optString("name", "");
                String apiNumber = company.optString("company_number", "");
                String apiJurisdiction = company.optString("jurisdiction_code", "");
                String apiType = company.optString("company_type", "");
                String apiAddress = company.optString("registered_address_in_full", "");

                // Debug output
                System.out.println("\n🔄 Comparing OCR data with API company #" + (i+1));
                System.out.println("🔄 Name Comparison:");
                System.out.println("OCR Value: '" + ocrName + "'");
                System.out.println("API Value: '" + apiName + "'");
                System.out.println("🔄 Number Comparison:");
                System.out.println("OCR Value: '" + ocrRegNumber + "'");
                System.out.println("API Value: '" + apiNumber + "'");
                System.out.println("🔄 Address Comparison:");
                System.out.println("OCR Value: '" + ocrAddress + "'");
                System.out.println("API Value: '" + apiAddress + "'");
                System.out.println("🔄 Jurisdiction Comparison:");
                System.out.println("OCR Value: '" + ocrJurisdiction + "'");
                System.out.println("API Value: '" + apiJurisdiction + "'");
                System.out.println("🔄 TYPE Comparison:");
                System.out.println("OCR Value: '" + ocrCompanyType + "'");
                System.out.println("API Value: '" + apiType + "'");

                // Get alternative names
                List<String> alternativeNames = new ArrayList<>();
                JSONArray altArray = company.optJSONArray("alternative_names");
                if (altArray != null) {
                    System.out.println("📝 Alternative Names:");
                    for (int j = 0; j < altArray.length(); j++) {
                        String altName = altArray.getString(j);
                        alternativeNames.add(altName);
                        System.out.println("- " + altName);
                    }
                }

                // Field-by-field fuzzy comparison
                Map<String, Boolean> matchResults = new HashMap<>();

                // Company name comparison
                boolean nameMatches = fuzzyMatch(ocrName, apiName) ||
                        alternativeNames.stream().anyMatch(alt -> fuzzyMatch(ocrName, alt));
                matchResults.put("company_name", nameMatches);

                // Registration number comparison
                boolean regNumberMatches = fuzzyMatch(ocrRegNumber, apiNumber);
                matchResults.put("registration_number", regNumberMatches);

                // Jurisdiction comparison with special handling
                boolean jurisdictionMatches = matchJurisdiction(ocrJurisdiction, apiJurisdiction);
                matchResults.put("jurisdiction", jurisdictionMatches);

                // Company type comparison
                boolean typeMatches = fuzzyMatch(ocrCompanyType, apiType);
                matchResults.put("company_type", typeMatches);

                // Address comparison
                boolean addressMatches = fuzzyMatch(ocrAddress, apiAddress);
                matchResults.put("address", addressMatches);

                // Print match results for debugging
                System.out.println("📊 Match Results:");
                for (Map.Entry<String, Boolean> entry : matchResults.entrySet()) {
                    System.out.println("- " + entry.getKey() + ": " + (entry.getValue() ? "✅ MATCH" : "❌ NO MATCH"));
                }

                // Count matches
                long matchedFields = matchResults.values().stream().filter(Boolean::booleanValue).count();
                System.out.println("📋 Total matched fields: " + matchedFields + "/5");



                // Check if we have enough matches - lowered from 3 to 2 to increase matches
                if (matchedFields >= 5) {
                    return VerificationResultDTO.builder()
                            .success(true)
                            .detectedCompanyName(apiName)
                            .message("✅ Company verified via AI match (score: " + matchedFields + "/5)")
                            .fieldMatches(matchResults)
                            .build();
                }
                else {
                    return VerificationResultDTO.builder()
                            .success(false)
                            .detectedCompanyName(apiName)
                            .message("⚠️ Not enough fields matched to verify the company.")
                            .fieldMatches(matchResults)
                            .build();
                }
            }

            return VerificationResultDTO.builder()
                    .success(false)
                    .message("❌ No matching companies found in OpenCorporates with sufficient field matches.")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return VerificationResultDTO.builder()
                    .success(false)
                    .message("❌ API Error: " + e.getMessage())
                    .build();
        }
    }

    // 🧠 Improved fuzzy match that removes accents and normalizes Arabic/Latin chars
    private boolean fuzzyMatch(String a, String b) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty()) return false;

        a = normalize(a);
        b = normalize(b);

        // For exact matches (after normalization)
        if (a.equals(b)) return true;

        // For substring matches
        if (a.contains(b) || b.contains(a)) return true;

        // For cases where strings share significant common content
        return calculateSimilarity(a, b) > 0.6; // Lowered to 60% similarity threshold for better matching
    }

    // Special handling for jurisdiction matching
    private boolean matchJurisdiction(String ocrJurisdiction, String apiJurisdiction) {
        if (ocrJurisdiction == null || apiJurisdiction == null ||
                ocrJurisdiction.isEmpty() || apiJurisdiction.isEmpty()) return false;

        // Normalize both strings
        String normalizedOcr = normalize(ocrJurisdiction);
        String normalizedApi = normalize(apiJurisdiction);

        // Direct match
        if (normalizedOcr.equals(normalizedApi)) return true;

        // Check for known mappings
        String mappedOcrCode = JURISDICTION_MAPPINGS.get(normalizedOcr.toLowerCase());
        if (mappedOcrCode != null) {
            // Check if the mapped code is in the API jurisdiction code
            if (normalizedApi.contains(mappedOcrCode)) return true;
        }

        // Check if OCR jurisdiction contains the API jurisdiction or vice versa
        return normalizedOcr.contains(normalizedApi) || normalizedApi.contains(normalizedOcr);
    }

    // 🔤 Normalize input: lowercase, remove diacritics, trim spaces and punctuation
    private String normalize(String input) {
        if (input == null) return "";
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "") // Remove accents
                .replaceAll("[^\\p{L}\\p{Nd}]+", "") // Remove non-letter/number
                .toLowerCase()
                .trim();
    }

    // Calculate string similarity using Levenshtein distance
    private double calculateSimilarity(String s1, String s2) {
        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0) return 1.0; // Both strings are empty

        return (double)(maxLength - levenshteinDistance(s1, s2)) / maxLength;
    }

    // Levenshtein distance implementation
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1)
                    );
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }
}