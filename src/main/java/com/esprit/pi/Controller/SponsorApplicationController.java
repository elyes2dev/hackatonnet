package com.esprit.pi.Controller;


import com.esprit.pi.DTO.SponsorApplicationDTO;
import com.esprit.pi.Service.ISponsorApplicationService;
import com.esprit.pi.entities.SponsorApplication;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/sponsor-application")
public class SponsorApplicationController {

    @Autowired
    ISponsorApplicationService sponsorApplicationService;

    @PostMapping("/{userId}/submit")
    public ResponseEntity<?> submitApplication(@PathVariable long userId,
                                               @RequestBody SponsorApplication application) {
        try {
            SponsorApplication savedApplication = sponsorApplicationService.submitApplication(userId, application);
            return ResponseEntity.ok(savedApplication);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/ai-verify")
    public ResponseEntity<Map<String, Object>> aiVerify(@PathVariable int id) {
        return sponsorApplicationService.aiVerifyApplication(id);
    }



    @GetMapping("/getallapplications")
    public ResponseEntity<List<SponsorApplication>> getAllApplications() {
        return ResponseEntity.ok(sponsorApplicationService.getAllApplications());
    }

    @GetMapping("/getapplicationbyid/{id}")
    public ResponseEntity<SponsorApplicationDTO> getApplicationById(@PathVariable int id) {
        SponsorApplicationDTO dto = sponsorApplicationService.getApplicationByIdDTO(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveApplication(@PathVariable int id) {
        try {
            SponsorApplication approvedApplication = sponsorApplicationService.approveApplication(id);
            return ResponseEntity.ok(approvedApplication);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectApplication(@PathVariable int id) {
        try {
            SponsorApplication rejectedApplication = sponsorApplicationService.rejectApplication(id);
            return ResponseEntity.ok(rejectedApplication);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteapplication/{id}")
    public ResponseEntity<Map<String, String>> deleteApplication(@PathVariable int id) {
        sponsorApplicationService.deleteApplication(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Application deleted successfully.");
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }


}
