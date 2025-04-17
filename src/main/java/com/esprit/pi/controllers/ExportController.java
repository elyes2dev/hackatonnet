// com/esprit/pi/controllers/ExportController.java
package com.esprit.pi.controllers;

import com.esprit.pi.services.CsvExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/export")
@Tag(name = "Export", description = "APIs for exporting data")
@CrossOrigin("*")
public class ExportController {

    @Autowired
    private CsvExportService csvExportService;

    @GetMapping("/mentors/csv/{hackathonId}")
    @Operation(summary = "Export mentors list to CSV")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully exported to CSV"),
            @ApiResponse(responseCode = "404", description = "Hackathon not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InputStreamResource> exportMentorsToCSV(@PathVariable Long hackathonId) {
        try {
            ByteArrayInputStream csvStream = csvExportService.exportMentorsToCSV(hackathonId);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=mentors.csv");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(new InputStreamResource(csvStream));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}