package com.esprit.pi.Service;

import org.springframework.stereotype.Service;

@Service
public class SponsorVerificationService {

    private final OCRService ocrService;
    private final CompanyVerifier companyVerifier;

    public SponsorVerificationService(OCRService ocrService, CompanyVerifier companyVerifier) {
        this.ocrService = ocrService;
        this.companyVerifier = companyVerifier;
    }

    public boolean verifyDocument(String filePath) {
        String extractedText = ocrService.extractTextFromFile(filePath);
        System.out.println("🔍 Extracted Text: " + extractedText);

        if (extractedText == null || extractedText.isEmpty()) {
            return false;
        }

        // Simple logic: just search for the first company name match
        String[] lines = extractedText.split("\\r?\\n");
        for (String line : lines) {
            if (companyVerifier.verifyCompany(line.trim())) {
                return true; // Found a valid company
            }
        }

        return false;
    }
}

