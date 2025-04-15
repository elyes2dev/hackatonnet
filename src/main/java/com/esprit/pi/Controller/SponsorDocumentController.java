package com.esprit.pi.Controller;

import com.esprit.pi.Service.SponsorVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sponsor-documents")
public class SponsorDocumentController {

    private final SponsorVerificationService verificationService;

    public SponsorDocumentController(SponsorVerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam("filePath") String filePath) {
        System.out.println("Received file path: " + filePath); // Debug print
        boolean isVerified = verificationService.verifyDocument(filePath);

        if (isVerified) {
            return ResponseEntity.ok("✅ Company verified successfully");
        } else {
            return ResponseEntity.status(404).body("❌ Company not found");
        }
    }
}

