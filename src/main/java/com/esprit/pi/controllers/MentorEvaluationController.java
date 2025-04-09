package com.esprit.pi.controllers;

import com.esprit.pi.entities.MentorEvaluation;
import com.esprit.pi.entities.Team;
import com.esprit.pi.entities.User;
import com.esprit.pi.services.MentorEvaluationService;
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
@RequestMapping("/api/mentor-evaluations")
@Tag(name = "Mentor Evaluations", description = "APIs for managing mentor evaluations")
@CrossOrigin("*")
public class MentorEvaluationController {

    @Autowired
    private MentorEvaluationService evaluationService;

    // Create
    @PostMapping
    @Operation(summary = "Create a new mentor evaluation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evaluation created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> createEvaluation(@RequestBody MentorEvaluation evaluation) {
        try {
            // Log received data for debugging
            System.out.println("Received evaluation with rating: " + evaluation.getRating());

            // Validate rating (0-5)
            if (evaluation.getRating() < 0 || evaluation.getRating() > 5) {
                throw new IllegalArgumentException("Rating must be between 0 and 5");
            }

            // Set static mentor ID 1
            User mentor = new User();
            mentor.setId(1L);
            evaluation.setMentor(mentor);

            // Set static team ID 1
            Team team = new Team();
            team.setId(1L);
            evaluation.setTeam(team);

            MentorEvaluation created = evaluationService.createEvaluation(evaluation);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Read
    @GetMapping
    @Operation(summary = "Get all mentor evaluations")
    public ResponseEntity<List<MentorEvaluation>> getAllEvaluations() {
        List<MentorEvaluation> evaluations = evaluationService.getAllEvaluations();
        return new ResponseEntity<>(evaluations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get mentor evaluation by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the evaluation"),
            @ApiResponse(responseCode = "404", description = "Evaluation not found")
    })
    public ResponseEntity<MentorEvaluation> getEvaluationById(@PathVariable Long id) {
        Optional<MentorEvaluation> evaluation = evaluationService.getEvaluationById(id);
        return evaluation.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/mentor/{mentorId}")
    @Operation(summary = "Get evaluations by mentor ID")
    public ResponseEntity<List<MentorEvaluation>> getEvaluationsByMentorId(@PathVariable Long mentorId) {
        List<MentorEvaluation> evaluations = evaluationService.getEvaluationsByMentorId(mentorId);
        return new ResponseEntity<>(evaluations, HttpStatus.OK);
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Get evaluations by team ID")
    public ResponseEntity<List<MentorEvaluation>> getEvaluationsByTeamId(@PathVariable Long teamId) {
        List<MentorEvaluation> evaluations = evaluationService.getEvaluationsByTeamId(teamId);
        return new ResponseEntity<>(evaluations, HttpStatus.OK);
    }

    @GetMapping("/rating/{minRating}")
    @Operation(summary = "Get evaluations by minimum rating")
    public ResponseEntity<List<MentorEvaluation>> getEvaluationsByMinimumRating(@PathVariable int minRating) {
        List<MentorEvaluation> evaluations = evaluationService.getEvaluationsByMinimumRating(minRating);
        return new ResponseEntity<>(evaluations, HttpStatus.OK);
    }

    // Update
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing mentor evaluation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evaluation updated successfully"),
            @ApiResponse(responseCode = "404", description = "Evaluation not found")
    })
    public ResponseEntity<?> updateEvaluation(@PathVariable Long id, @RequestBody MentorEvaluation evaluation) {
        Optional<MentorEvaluation> existingEvaluation = evaluationService.getEvaluationById(id);
        if (existingEvaluation.isPresent()) {
            try {
                evaluation.setId(id);
                User mentor = new User();
                mentor.setId(1L);
                evaluation.setMentor(mentor);

                // Set static team ID 1
                Team team = new Team();
                team.setId(1L);
                evaluation.setTeam(team);
                MentorEvaluation updated = evaluationService.updateEvaluation(evaluation);
                return new ResponseEntity<>(updated, HttpStatus.OK);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a mentor evaluation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evaluation deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Evaluation not found")
    })
    public ResponseEntity<Void> deleteEvaluation(@PathVariable Long id) {
        Optional<MentorEvaluation> existingEvaluation = evaluationService.getEvaluationById(id);
        if (existingEvaluation.isPresent()) {
            evaluationService.deleteEvaluation(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}