package com.esprit.pi.controllers;

import com.esprit.pi.entities.TeamSubmission;
import com.esprit.pi.services.TeamSubmissionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/team-submissions")
@CrossOrigin(origins = "http://localhost:4200")
public class TeamSubmissionController {

    @Autowired
    private TeamSubmissionService teamSubmissionService;

    @GetMapping
    public ResponseEntity<List<TeamSubmission>> getAllTeamSubmissions() {
        List<TeamSubmission> submissions = teamSubmissionService.getAllTeamSubmissions();
        // Briser manuellement les références inverses pour éviter la boucle
        submissions.forEach(submission -> {
            if (submission.getEvaluations() != null) {
                submission.getEvaluations().forEach(eval -> {
                    eval.setTeamSubmission(null); // Coupe la référence inverse
                });
            }
        });
        System.out.println("TeamSubmissions après traitement : " + submissions);
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamSubmission> getTeamSubmissionById(@PathVariable Long id) {
        Optional<TeamSubmission> submission = teamSubmissionService.getTeamSubmissionById(id);
        submission.ifPresent(s -> {
            if (s.getEvaluations() != null) {
                s.getEvaluations().forEach(eval -> eval.setTeamSubmission(null));
            }
            System.out.println("TeamSubmission par ID : " + s);
        });
        return submission.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/team-member/{teamMemberId}")
    public ResponseEntity<List<TeamSubmission>> getTeamSubmissionsByTeamMemberId(@PathVariable Long teamMemberId) {
        List<TeamSubmission> submissions = teamSubmissionService.getTeamSubmissionByTeamMemberId(teamMemberId);
        submissions.forEach(submission -> {
            if (submission.getEvaluations() != null) {
                submission.getEvaluations().forEach(eval -> eval.setTeamSubmission(null));
            }
        });
        System.out.println("TeamSubmissions par teamMemberId : " + submissions);
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TeamSubmission>> searchTeamSubmissionsByProjectName(@RequestParam String keyword) {
        List<TeamSubmission> submissions = teamSubmissionService.searchTeamSubmissionsByProjectName(keyword);
        submissions.forEach(submission -> {
            if (submission.getEvaluations() != null) {
                submission.getEvaluations().forEach(eval -> eval.setTeamSubmission(null));
            }
        });
        System.out.println("TeamSubmissions par recherche : " + submissions);
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createTeamSubmission(@RequestBody TeamSubmission teamSubmission) {
        try {
            System.out.println("Reçu pour création : " + teamSubmission);
            TeamSubmission createdSubmission = teamSubmissionService.createTeamSubmission(teamSubmission);
            return new ResponseEntity<>("Soumission créée avec succès", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur IllegalArgumentException : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne : " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTeamSubmission(@PathVariable Long id, @RequestBody TeamSubmission teamSubmission) {
        try {
            System.out.println("Reçu pour mise à jour : " + teamSubmission);
            TeamSubmission updatedSubmission = teamSubmissionService.updateTeamSubmission(id, teamSubmission);
            return new ResponseEntity<>(updatedSubmission, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Élément non trouvé.", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeamSubmission(@PathVariable("id") Long id) {
        try {
            teamSubmissionService.deleteTeamSubmission(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    class ErrorResponse {
        private String code;
        private String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}