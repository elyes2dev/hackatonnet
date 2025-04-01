package com.esprit.pi.controllers;
import com.esprit.pi.services.QuizCertificateService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/certificates")
public class QuizCertificateController {

    private final QuizCertificateService quizCertificateService;

    public QuizCertificateController(QuizCertificateService quizCertificateService) {
        this.quizCertificateService = quizCertificateService;
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadCertificate(
            @RequestParam String username,
            @RequestParam String quizTitle,
            @RequestParam int score) {

        if (score > 5) {  // Only generate certificate if score is greater than 5
            ByteArrayInputStream certificateStream = quizCertificateService.generateCertificate(username, quizTitle, score);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=certificate.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(certificateStream));
        } else {
            return ResponseEntity.status(403).body(null);  // Forbidden if score is less than or equal to 5
        }
    }
}

