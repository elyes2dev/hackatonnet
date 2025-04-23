package com.esprit.pi.controllers;

import com.esprit.pi.entities.Hackathon;
import com.esprit.pi.entities.ListMentor;
import com.esprit.pi.entities.User;
import com.esprit.pi.services.ListMentorService;
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
@RequestMapping("/api/list-mentors")
@Tag(name = "List Mentors", description = "APIs for managing hackathon mentors")
public class ListMentorController {

    @Autowired
    private ListMentorService listMentorService;

    // Create
    @PostMapping
    @Operation(summary = "Create a new mentor listing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mentor listing created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<ListMentor> createListMentor(@RequestBody ListMentor listMentor) {

        Long userId = listMentor.getMentor().getId();
        Long hackathonId = listMentor.getHackathon().getId();
        int numberOfTeams = listMentor.getNumberOfTeams();

        // Call the service method with the extracted parameters
        ListMentor created = listMentorService.createListMentor(userId, hackathonId, numberOfTeams);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    // Read
    @GetMapping
    @Operation(summary = "Get all mentor listings")
    public ResponseEntity<List<ListMentor>> getAllListMentors() {
        List<ListMentor> listMentors = listMentorService.getAllListMentors();
        return new ResponseEntity<>(listMentors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get mentor listing by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the mentor listing"),
            @ApiResponse(responseCode = "404", description = "Mentor listing not found")
    })
    public ResponseEntity<ListMentor> getListMentorById(@PathVariable Long id) {
        Optional<ListMentor> listMentor = listMentorService.getListMentorById(id);
        return listMentor.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/mentor/{mentorId}")
    @Operation(summary = "Get listings by mentor ID")
    public ResponseEntity<List<ListMentor>> getListMentorsByMentorId(@PathVariable Long mentorId) {
        List<ListMentor> listMentors = listMentorService.getListMentorsByMentorId(mentorId);
        return new ResponseEntity<>(listMentors, HttpStatus.OK);
    }

    @GetMapping("/hackathon/{hackathonId}")
    @Operation(summary = "Get listings by hackathon ID")
    public ResponseEntity<List<ListMentor>> getListMentorsByHackathonId(@PathVariable Long hackathonId) {
        List<ListMentor> listMentors = listMentorService.getListMentorsByHackathonId(hackathonId);
        return new ResponseEntity<>(listMentors, HttpStatus.OK);
    }

    // Update
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing mentor listing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentor listing updated successfully"),
            @ApiResponse(responseCode = "404", description = "Mentor listing not found")
    })
    public ResponseEntity<ListMentor> updateListMentor(@PathVariable Long id, @RequestBody ListMentor listMentor) {
        Optional<ListMentor> existingListMentor = listMentorService.getListMentorById(id);
        if (existingListMentor.isPresent()) {
            listMentor.setId(id);
            // From your backend controller

            int numberOfTeams = listMentor.getNumberOfTeams();



            ListMentor updated = listMentorService.updateListMentor(id,numberOfTeams);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a mentor listing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mentor listing deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Mentor listing not found")
    })
    public ResponseEntity<Void> deleteListMentor(@PathVariable Long id) {
        Optional<ListMentor> existingListMentor = listMentorService.getListMentorById(id);
        if (existingListMentor.isPresent()) {
            listMentorService.deleteListMentor(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}