package com.esprit.pi.controllers;

import com.esprit.pi.entities.PreviousExperience;
import com.esprit.pi.services.PreviousExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/previous-experiences")
@Tag(name = "Previous Experiences", description = "APIs for managing mentor's previous experiences")
public class PreviousExperienceController {

    @Autowired
    private PreviousExperienceService experienceService;

    // Create
    @PostMapping("/application/{applicationId}")
    @Operation(summary = "Create a new previous experience for a mentor application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Experience created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    public ResponseEntity<PreviousExperience> createExperience(
            @PathVariable Long applicationId,
            @RequestBody PreviousExperience experience) {
        PreviousExperience created = experienceService.createExperience(applicationId, experience);
        if (created != null) {
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Read
    @GetMapping
    @Operation(summary = "Get all previous experiences")
    public ResponseEntity<List<PreviousExperience>> getAllExperiences() {
        List<PreviousExperience> experiences = experienceService.getAllExperiences();
        return new ResponseEntity<>(experiences, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get previous experience by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the experience"),
            @ApiResponse(responseCode = "404", description = "Experience not found")
    })
    public ResponseEntity<PreviousExperience> getExperienceById(@PathVariable Long id) {
        Optional<PreviousExperience> experience = experienceService.getExperienceById(id);
        return experience.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/application/{applicationId}")
    @Operation(summary = "Get experiences by application ID")
    public ResponseEntity<List<PreviousExperience>> getExperiencesByApplicationId(@PathVariable Long applicationId) {
        List<PreviousExperience> experiences = experienceService.getExperiencesByApplicationId(applicationId);
        return new ResponseEntity<>(experiences, HttpStatus.OK);
    }

    @GetMapping("/year/{year}")
    @Operation(summary = "Get experiences by year")
    public ResponseEntity<List<PreviousExperience>> getExperiencesByYear(@PathVariable int year) {
        List<PreviousExperience> experiences = experienceService.getExperiencesByYear(year);
        return new ResponseEntity<>(experiences, HttpStatus.OK);
    }

    @GetMapping("/hackathon")
    @Operation(summary = "Get experiences by hackathon name")
    public ResponseEntity<List<PreviousExperience>> getExperiencesByHackathonName(@RequestParam String keyword) {
        List<PreviousExperience> experiences = experienceService.getExperiencesByHackathonName(keyword);
        return new ResponseEntity<>(experiences, HttpStatus.OK);
    }

    // Update
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing previous experience")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Experience updated successfully"),
            @ApiResponse(responseCode = "404", description = "Experience not found")
    })
    public ResponseEntity<PreviousExperience> updateExperience(
            @PathVariable Long id,
            @RequestBody PreviousExperience experience) {
        PreviousExperience updated = experienceService.updateExperience(id, experience);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a previous experience")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Experience deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Experience not found")
    })
    public ResponseEntity<Void> deleteExperience(@PathVariable Long id) {
        boolean deleted = experienceService.deleteExperience(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    @DeleteMapping("/application/{applicationId}")
    @Operation(summary = "Delete all experiences for an application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Experiences deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    public ResponseEntity<Void> deleteExperiencesByApplicationId(@PathVariable Long applicationId) {
        boolean deleted = experienceService.deleteExperiencesByApplicationId(applicationId);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}