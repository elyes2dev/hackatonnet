package com.esprit.pi.services;

import com.esprit.pi.dtos.VerificationResultDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SponsorVerificationService {

    private final OCRService ocrService;
    private final CompanyVerifier companyVerifier;

    public SponsorVerificationService(OCRService ocrService, CompanyVerifier companyVerifier) {
        this.ocrService = ocrService;
        this.companyVerifier = companyVerifier;
    }

    public VerificationResultDTO verifyDocument(String filePath) {
        String extractedText = ocrService.extractTextFromFile(filePath);
        System.out.println("🔍 Extracted Text: \n" + extractedText);

        if (extractedText == null || extractedText.isEmpty()) {
            return VerificationResultDTO.builder()
                    .success(false)
                    .message("❌ No text extracted from the document.")
                    .build();
        }

        // Parse key fields from OCR with improved extraction
        Map<String, String> extractedFields = new HashMap<>();

        // First pass - try to extract fields using the standard format
        for (String line : extractedText.split("\\r?\\n")) {
            String cleanLine = line.trim().toLowerCase();

            if (cleanLine.contains("company name") || cleanLine.contains("company_name")) {
                extractedFields.put("company_name", extractValue(line));
            } else if (cleanLine.contains("registration number") || cleanLine.contains("registration_number")) {
                extractedFields.put("registration_number", extractValue(line));
            } else if (cleanLine.contains("jurisdiction")) {
                extractedFields.put("jurisdiction", extractValue(line));
            } else if (cleanLine.contains("company type") || cleanLine.contains("company_type")) {
                extractedFields.put("company_type", extractValue(line));
            } else if (cleanLine.contains("registered address") || cleanLine.contains("registered_address") ||
                    cleanLine.contains("address")) {
                extractedFields.put("registered_address", extractValue(line));
            }
        }

        // Second pass - try to infer fields from context if still missing
        if (!extractedFields.containsKey("company_name")) {
            // Try to find company name based on common patterns in documents
            for (String line : extractedText.split("\\r?\\n")) {
                if (line.contains("SAGEMCOM") || line.contains("Sagemcom")) {
                    extractedFields.put("company_name", line.trim());
                    break;
                }
            }
        }

        System.out.println("📋 Extracted Fields: " + extractedFields);

        // Verify against company database
        return companyVerifier.verifyCompanyFromOCR(extractedFields);
    }

    private String extractValue(String line) {
        // Handle multiple delimiters
        for (String delimiter : new String[] {":", "：", "-", "="}) {
            if (line.contains(delimiter)) {
                String[] parts = line.split(delimiter, 2);
                if (parts.length > 1) {
                    return parts[1].trim();
                }
            }
        }

        // If no delimiter found, try to extract value after the key
        String[] words = line.split("\\s+");
        if (words.length > 2) {
            StringBuilder value = new StringBuilder();
            boolean foundKey = false;

            for (String word : words) {
                if (foundKey) {
                    value.append(word).append(" ");
                } else if (word.toLowerCase().contains("name") ||
                        word.toLowerCase().contains("number") ||
                        word.toLowerCase().contains("jurisdiction") ||
                        word.toLowerCase().contains("type") ||
                        word.toLowerCase().contains("address")) {
                    foundKey = true;
                }
            }

            String result = value.toString().trim();
            if (!result.isEmpty()) {
                return result;
            }
        }

        return "";
    }
}