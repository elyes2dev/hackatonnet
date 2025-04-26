package com.esprit.pi.controllers;

import com.esprit.pi.dtos.TicketDTO;
import com.esprit.pi.entities.Ticket;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.UserRepository;
import com.esprit.pi.services.ITicketService;
import com.esprit.pi.services.SummarizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private ITicketService ticketService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private final SummarizerService mlService;

    public TicketController(SummarizerService mlService) {
        this.mlService = mlService;
    }

    @PostMapping
    public Ticket createTicket(@RequestBody TicketDTO dto) {
        User user = userRepository.findById(dto.userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = new Ticket();
        ticket.setDescription(dto.description);
        ticket.setStatus(dto.status);
        ticket.setUser(user);

        return ticketService.createTicket(ticket);
    }


    @PutMapping("/{id}")
    public Ticket updateTicket(@PathVariable Long id, @RequestBody Ticket ticket) {
        return ticketService.updateTicket(id, ticket);
    }

    @DeleteMapping("/{id}")
    public void deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
    }

    @GetMapping("/{id}")
    public Ticket getTicket(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("/user/{userId}")
    public List<Ticket> getTicketsByUser(@PathVariable Long userId) {
        return ticketService.getTicketsByUserId(userId);
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> analyze(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("{\"error\":\"Field 'text' is required\"}");
        }

        String resultJson = mlService.analyzeText(text);
        return ResponseEntity.ok(resultJson);
    }
}

