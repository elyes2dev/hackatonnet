package com.esprit.pi.Controller;

import com.esprit.pi.Service.IPrizeService;
import com.esprit.pi.entities.Prize;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/prize")
public class PrizeController {

    @Autowired
    IPrizeService prizeService;

    @PostMapping("/{sponsorId}/{hackathonId}/create")
    public ResponseEntity<?> createPrize(@PathVariable long sponsorId,
                                         @PathVariable long hackathonId,
                                         @RequestBody Prize prize) {
        try {
            Prize createdPrize = prizeService.createPrize(sponsorId, hackathonId, prize);
            return ResponseEntity.ok(createdPrize);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getallprizes")
    public ResponseEntity<List<Prize>> getAllPrizes() {
        return ResponseEntity.ok(prizeService.getAllPrizes());
    }

    @GetMapping("/getprizebyid/{id}")
    public ResponseEntity<?> getPrizeById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(prizeService.getPrizeById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getprizebyhackathon/{hackathonId}")
    public ResponseEntity<?> getPrizesByHackathon(@PathVariable long hackathonId) {
        List<Prize> prizes = prizeService.getPrizesByHackathon(hackathonId);
        if (prizes.isEmpty()) {
            return ResponseEntity.status(404).body("No prizes found for this hackathon.");
        }
        return ResponseEntity.ok(prizes);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approvePrize(@PathVariable long id) {
        try {
            Prize approvedPrize = prizeService.approvePrize(id);
            return ResponseEntity.ok(approvedPrize);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectPrize(@PathVariable long id) {
        try {
            Prize rejectedPrize = prizeService.rejectPrize(id);
            return ResponseEntity.ok(rejectedPrize);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePrize(@PathVariable long id) {
        try {
            prizeService.deletePrize(id);
            return ResponseEntity.ok("Prize deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
