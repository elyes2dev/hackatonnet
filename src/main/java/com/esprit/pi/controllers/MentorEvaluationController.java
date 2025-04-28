package com.esprit.pi.controllers;

import com.esprit.pi.entities.MentorEvaluation;
import com.esprit.pi.entities.Team;
import com.esprit.pi.entities.User;
import com.esprit.pi.services.MentorEvaluationService;
import com.esprit.pi.services.UserService;
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
public class MentorEvaluationController {

    @Autowired
    private MentorEvaluationService evaluationService;


    @Autowired
    private UserService userService;
    // Create
    @PostMapping
    @Operation(summary = "Create a new mentor evaluation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evaluation created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> createEvaluation(@RequestBody MentorEvaluation evaluation) {
        try {
            // Validate rating (0-5)
            if (evaluation.getRating() < 0 || evaluation.getRating() > 5) {
                throw new IllegalArgumentException("Rating must be between 0 and 5");
            }

            // Validate mentor exists (use the mentorId from the request payload)
            if (evaluation.getMentor() == null || evaluation.getMentor().getId() == null) {
                throw new IllegalArgumentException("Mentor ID is required");
            }

            Long mentorId = evaluation.getMentor().getId();
            User mentor = userService.getUserById(mentorId)
                    .orElseThrow(() -> new IllegalArgumentException("Mentor with ID " + mentorId + " not found"));

            // Validate team exists (use the teamId from the request payload)
            if (evaluation.getTeam() == null || evaluation.getTeam().getId() == null) {
                throw new IllegalArgumentException("Team ID is required");
            }

            // Team validation would ideally use a TeamService
            // For now we'll assume the team exists with the given ID
            // If you have a TeamService, add a validation here

            MentorEvaluation created = evaluationService.createEvaluation(evaluation);

            // Update points and badge for the specific mentor
            userService.updateUserPointsAndBadge(mentorId);

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
// Update
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvaluation(@PathVariable Long id, @RequestBody MentorEvaluation evaluation) {
        Optional<MentorEvaluation> existingEvaluationOpt = evaluationService.getEvaluationById(id);
        if (existingEvaluationOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            // Get the existing evaluation
            MentorEvaluation existingEvaluation = existingEvaluationOpt.get();

            // Update only the fields that should change
            existingEvaluation.setFeedbackText(evaluation.getFeedbackText());
            existingEvaluation.setRating(evaluation.getRating());

            // Validate rating
            if (existingEvaluation.getRating() < 0 || existingEvaluation.getRating() > 5) {
                throw new IllegalArgumentException("Rating must be between 0 and 5");
            }

            // Preserve relationships - don't overwrite them
            // (Mentor and Team shouldn't change during an update)

            MentorEvaluation updated = evaluationService.updateEvaluation(existingEvaluation);

            // Update points and badge for the specific mentor
            Long mentorId = existingEvaluation.getMentor().getId();
            userService.updateUserPointsAndBadge(mentorId);

            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
            // Store mentor ID before deletion
            Long mentorId = existingEvaluation.get().getMentor().getId();

            evaluationService.deleteEvaluation(id);

            // Update points and badge for the specific mentor
            userService.updateUserPointsAndBadge(mentorId);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    // Add a new endpoint to get mentor's current badge level
    @GetMapping("/mentor/{mentorId}/badge")
    @Operation(summary = "Get mentor's current badge level")
    public ResponseEntity<User.BadgeLevel> getMentorBadge(@PathVariable Long mentorId) {
        Optional<User> mentor = userService.getUserById(mentorId);
        return mentor.map(user -> new ResponseEntity<>(user.getBadge(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


}