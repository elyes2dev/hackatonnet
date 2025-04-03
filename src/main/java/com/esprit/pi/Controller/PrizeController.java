package com.esprit.pi.Controller;

import com.esprit.pi.DTO.PrizeDTO;
import com.esprit.pi.DTO.SponsorInfoDTO;
import com.esprit.pi.Service.IPrizeService;
import com.esprit.pi.entities.Prize;
import com.esprit.pi.entities.User;
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
            return ResponseEntity.ok(prizeService.getPrizeByIdDTO(id));
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

    @GetMapping("/getprizebysponsor/{sponsorId}")
    public List<PrizeDTO> getPrizesBySponsor(@PathVariable Long sponsorId) {
        return prizeService.getPrizesBySponsorDTO(sponsorId);
    }

    @GetMapping("/getprizebycategory/{category}")
    public List<Prize> getPrizesByCategory(@PathVariable Prize.PrizeCategory category) {
        return prizeService.getPrizesByCategory(category);
    }

    @GetMapping("/sponsor/{sponsorId}/category/{category}")
    public List<Prize> getPrizesBySponsorAndCategory(@PathVariable Long sponsorId, @PathVariable Prize.PrizeCategory category) {
        return prizeService.getPrizesBySponsorAndCategory(sponsorId, category);
    }

    @GetMapping("/hackathon/{hackathonId}/category/{category}")
    public List<Prize> getPrizesByHackathonAndCategory(@PathVariable Long hackathonId, @PathVariable Prize.PrizeCategory category) {
        return prizeService.getPrizesByHackathonAndCategory(hackathonId, category);
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

    @PutMapping("/{prizeId}/cancel/{sponsorId}")
    public ResponseEntity<Prize> cancelPrize(@PathVariable long prizeId, @PathVariable long sponsorId) {
        Prize canceledPrize = prizeService.cancelPrize(prizeId, sponsorId);
        return ResponseEntity.ok(canceledPrize);
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
    // Get sponsors by badge (sorted) for a specific hackathon
    @GetMapping("/sponsors-by-badge/{hackathonId}")
    public ResponseEntity<List<SponsorInfoDTO>> getSponsorsByBadge(@PathVariable Long hackathonId) {
        return ResponseEntity.ok(prizeService.getSponsorsByHackathon(hackathonId));
    }

}
