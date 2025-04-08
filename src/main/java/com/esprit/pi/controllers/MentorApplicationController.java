package com.esprit.pi.controllers;

import com.esprit.pi.entities.ApplicationStatus;
import com.esprit.pi.entities.MentorApplication;
import com.esprit.pi.services.FileStorageService;
import com.esprit.pi.services.MentorApplicationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mentor-applications")
@Tag(name = "Mentor Applications", description = "APIs for mentor application management")
@CrossOrigin("*")
public class MentorApplicationController {

    @Autowired
    private MentorApplicationService applicationService;

    @Autowired
    private FileStorageService fileStorageService;

    // Create
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new mentor application with file uploads")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "File upload failed")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(type = "object"),
                    encoding = {
                            @Encoding(name = "application", contentType = MediaType.APPLICATION_JSON_VALUE),
                            @Encoding(name = "cvFile", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE),
                            @Encoding(name = "uploadPaperFile", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    }
            )
    )
    public ResponseEntity<MentorApplication> createApplication(
            @RequestPart("application") @Parameter(description = "Mentor application data in JSON format") String applicationJson,
            @RequestPart(value = "cvFile", required = false) @Parameter(description = "CV file (PDF/DOCX)") MultipartFile cvFile,
            @RequestPart(value = "uploadPaperFile", required = false) @Parameter(description = "Supporting documents file") MultipartFile uploadPaperFile) {

        try {
            // Convert JSON string to MentorApplication object
            ObjectMapper objectMapper = new ObjectMapper();
            MentorApplication application = objectMapper.readValue(applicationJson, MentorApplication.class);

            // Handle file uploads
            if (cvFile != null && !cvFile.isEmpty()) {
                String cvFilename = fileStorageService.storeFile(cvFile);
                application.setCv(cvFilename);
            }

            if (uploadPaperFile != null && !uploadPaperFile.isEmpty()) {
                String paperFilename = fileStorageService.storeFile(uploadPaperFile);
                application.setUploadPaper(paperFilename);
            }

            MentorApplication created = applicationService.createApplication(application);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Read
    @GetMapping
    @Operation(summary = "Get all mentor applications")
    public ResponseEntity<List<MentorApplication>> getAllApplications() {
        List<MentorApplication> applications = applicationService.getAllApplications();
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    // Read
    @GetMapping("/{id}")
    @Operation(summary = "Get a mentor application by ID")
    @ApiResponse(responseCode = "200", description = "Application found")
    @ApiResponse(responseCode = "404", description = "Application not found")
    public ResponseEntity<MentorApplication> getApplication(@PathVariable Long id) {
        return applicationService.getApplicationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Download file
    @GetMapping("/files/{filename:.+}")
    @Operation(summary = "Download a file associated with an application")
    @ApiResponse(responseCode = "200", description = "File downloaded successfully")
    @ApiResponse(responseCode = "404", description = "File not found")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String filename) {
        try {
            byte[] fileContent = fileStorageService.loadFileAsResource(filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
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