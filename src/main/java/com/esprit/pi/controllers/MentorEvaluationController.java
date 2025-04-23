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

            // Validate mentor exists
            // Set static mentor and team with ID = 1
            User mentor = userService.getUserById(1L)
                    .orElseThrow(() -> new IllegalArgumentException("Mentor with ID 1 not found"));
            Team team = new Team(); // You can inject a TeamService if needed
            team.setId(1L); // Minimal setup, assumes team exists in DB

            evaluation.setMentor(mentor);
            evaluation.setTeam(team);

            MentorEvaluation created = evaluationService.createEvaluation(evaluation);

            // Always refresh user with ID 1
            userService.updateUserPointsAndBadge(1L);

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
            // (unless you specifically want to allow changing mentor/team)

            MentorEvaluation updated = evaluationService.updateEvaluation(existingEvaluation);

            // Always refresh user with ID 1
            userService.updateUserPointsAndBadge(1L);

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
            evaluationService.deleteEvaluation(id);

            // Always refresh user with ID 1
            userService.updateUserPointsAndBadge(1L);

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