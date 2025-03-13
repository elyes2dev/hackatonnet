package com.esprit.pi.controllers;

import com.esprit.pi.entities.MonitorEvaluation;
import com.esprit.pi.services.MonitorEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/monitor-evaluations")
public class MonitorEvaluationController {

    @Autowired
    private MonitorEvaluationService evaluationService;

    // Create
    @PostMapping
    public ResponseEntity<?> createEvaluation(@RequestBody MonitorEvaluation evaluation) {
        try {
            MonitorEvaluation created = evaluationService.createEvaluation(evaluation);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Read
    @GetMapping
    public ResponseEntity<List<MonitorEvaluation>> getAllEvaluations() {
        List<MonitorEvaluation> evaluations = evaluationService.getAllEvaluations();
        return new ResponseEntity<>(evaluations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonitorEvaluation> getEvaluationById(@PathVariable Long id) {
        Optional<MonitorEvaluation> evaluation = evaluationService.getEvaluationById(id);
        return evaluation.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<MonitorEvaluation>> getEvaluationsByMentorId(@PathVariable Long mentorId) {
        List<MonitorEvaluation> evaluations = evaluationService.getEvaluationsByMentorId(mentorId);
        return new ResponseEntity<>(evaluations, HttpStatus.OK);
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<MonitorEvaluation>> getEvaluationsByTeamId(@PathVariable Long teamId) {
        List<MonitorEvaluation> evaluations = evaluationService.getEvaluationsByTeamId(teamId);
        return new ResponseEntity<>(evaluations, HttpStatus.OK);
    }

    @GetMapping("/rating/{minRating}")
    public ResponseEntity<List<MonitorEvaluation>> getEvaluationsByMinimumRating(@PathVariable int minRating) {
        List<MonitorEvaluation> evaluations = evaluationService.getEvaluationsByMinimumRating(minRating);
        return new ResponseEntity<>(evaluations, HttpStatus.OK);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvaluation(@PathVariable Long id, @RequestBody MonitorEvaluation evaluation) {
        Optional<MonitorEvaluation> existingEvaluation = evaluationService.getEvaluationById(id);
        if (existingEvaluation.isPresent()) {
            try {
                evaluation.setId(id);
                MonitorEvaluation updated = evaluationService.updateEvaluation(evaluation);
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
    public ResponseEntity<Void> deleteEvaluation(@PathVariable Long id) {
        Optional<MonitorEvaluation> existingEvaluation = evaluationService.getEvaluationById(id);
        if (existingEvaluation.isPresent()) {
            evaluationService.deleteEvaluation(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}