package com.esprit.pi.controllers;

import com.esprit.pi.dtos.VerificationResultDTO;
import com.esprit.pi.services.SponsorVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sponsor-documents")
public class SponsorDocumentController {

    private final SponsorVerificationService verificationService;

    public SponsorDocumentController(SponsorVerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/verify")
    public ResponseEntity<VerificationResultDTO> verify(@RequestParam("filePath") String filePath) {
        System.out.println("📩 Received file path for AI verification: " + filePath);

        VerificationResultDTO result = verificationService.verifyDocument(filePath);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(422).body(result);
        }
    }
}