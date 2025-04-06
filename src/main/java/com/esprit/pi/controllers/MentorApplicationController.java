package com.esprit.pi.controllers;

import com.esprit.pi.entities.ApplicationStatus;
import com.esprit.pi.entities.MentorApplication;
import com.esprit.pi.services.MentorApplicationService;
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
@RequestMapping("/api/mentor-applications")
@Tag(name = "Mentor Applications", description = "APIs for mentor application management")
@CrossOrigin("*")
public class MentorApplicationController {

    @Autowired
    private MentorApplicationService applicationService;

    // Create
    @PostMapping
    @Operation(summary = "Create a new mentor application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<MentorApplication> createApplication(@RequestBody MentorApplication application) {
        MentorApplication created = applicationService.createApplication(application);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Read
    @GetMapping
    @Operation(summary = "Get all mentor applications")
    public ResponseEntity<List<MentorApplication>> getAllApplications() {
        List<MentorApplication> applications = applicationService.getAllApplications();
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get mentor application by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the application"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    public ResponseEntity<MentorApplication> getApplicationById(@PathVariable Long id) {
        Optional<MentorApplication> application = applicationService.getApplicationById(id);
        return application.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/mentor/{mentorId}")
    @Operation(summary = "Get applications by mentor ID")
    public ResponseEntity<List<MentorApplication>> getApplicationsByMentorId(@PathVariable Long mentorId) {
        List<MentorApplication> applications = applicationService.getApplicationsByUserId(mentorId);
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get applications by status")
    public ResponseEntity<List<MentorApplication>> getApplicationsByStatus(@PathVariable ApplicationStatus status) {
        List<MentorApplication> applications = applicationService.getApplicationsByStatus(status);
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    @GetMapping("/experience")
    @Operation(summary = "Get applications by previous experience")
    public ResponseEntity<List<MentorApplication>> getApplicationsByExperience(@RequestParam boolean hasPreviousExperience) {
        List<MentorApplication> applications = applicationService.getApplicationsByExperience(hasPreviousExperience);
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    // Update
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing mentor application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application updated successfully"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    public ResponseEntity<MentorApplication> updateApplication(@PathVariable Long id, @RequestBody MentorApplication application) {
        MentorApplication updated = applicationService.updateApplication(id, application);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Update status
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update mentor application status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    public ResponseEntity<MentorApplication> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status) {
        MentorApplication updated = applicationService.updateApplicationStatus(id, status);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a mentor application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Application deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        boolean deleted = applicationService.deleteApplication(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}