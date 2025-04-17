package com.esprit.pi.controllers;

import com.esprit.pi.entities.ApplicationStatus;
import com.esprit.pi.entities.MentorApplication;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.UserRepository;
import com.esprit.pi.services.FileStorageService;
import com.esprit.pi.services.MentorApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Autowired
    private UserRepository userRepository;

    // Create
    @PostMapping
    @Operation(summary = "Create a new mentor application with file upload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<MentorApplication> createApplication(
            @Parameter(
                    description = "Mentor application details",
                    schema = @Schema(implementation = MentorApplication.class)
            )
            @ModelAttribute MentorApplication application,
            @Parameter(description = "CV file upload", content = @Content(mediaType = "application/pdf"))
            @RequestParam("cvFile") MultipartFile cvFile,
            @Parameter(description = "Upload paper (optional)", content = @Content(mediaType = "application/pdf"))
            @RequestParam(value = "uploadPaperFile", required = false) MultipartFile uploadPaperFile) {
        try {
            // Static user assignment
            User staticUser = userRepository.findById(1L).orElse(null);
            if (staticUser == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Or throw an error
            }

            application.setUser(staticUser); // Assuming MentorApplication has a `user` field

            MentorApplication created = applicationService.createApplication(application, cvFile, uploadPaperFile);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Update with file upload
    @PutMapping("/{id}")
    @Operation(summary = "Update a mentor application with optional file upload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application updated successfully"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    public ResponseEntity<MentorApplication> updateApplication(
            @PathVariable Long id,
            @ModelAttribute MentorApplication application,
            @RequestParam(value = "cvFile", required = false) MultipartFile cvFile,
            @RequestParam(value = "uploadPaperFile", required = false) MultipartFile uploadPaperFile) {
        try {
            MentorApplication updated = applicationService.updateApplication(id, application, cvFile, uploadPaperFile);
            if (updated != null) {
                return new ResponseEntity<>(updated, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // File download endpoints
    @GetMapping("/{id}/cv")
    @Operation(summary = "Download CV file")
    public ResponseEntity<Resource> downloadCv(@PathVariable Long id) {
        Optional<MentorApplication> application = applicationService.getApplicationById(id);
        if (application.isPresent() && application.get().getCv() != null) {
            Resource resource = applicationService.getFileAsResource(application.get().getCv());

            // Determine content type
            String contentType = null;
            try {
                contentType = Files.probeContentType(Paths.get(resource.getFile().getAbsolutePath()));
            } catch (IOException e) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}/upload-paper")
    @Operation(summary = "Download upload paper file")
    public ResponseEntity<Resource> downloadUploadPaper(@PathVariable Long id) {
        Optional<MentorApplication> application = applicationService.getApplicationById(id);
        if (application.isPresent() && application.get().getUploadPaper() != null) {
            Resource resource = applicationService.getFileAsResource(application.get().getUploadPaper());

            // Determine content type
            String contentType = null;
            try {
                contentType = Files.probeContentType(Paths.get(resource.getFile().getAbsolutePath()));
            } catch (IOException e) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Read
    @GetMapping
    @Operation(summary = "Get all mentor applications")
    public ResponseEntity<List<MentorApplication>> getAllApplications() {
        List<MentorApplication> applications = applicationService.getAllApplications();
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a mentor application by ID")
    @ApiResponse(responseCode = "200", description = "Application found")
    @ApiResponse(responseCode = "404", description = "Application not found")
    public ResponseEntity<MentorApplication> getApplication(@PathVariable Long id) {
        return applicationService.getApplicationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) throws IOException {
        boolean deleted = applicationService.deleteApplication(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}