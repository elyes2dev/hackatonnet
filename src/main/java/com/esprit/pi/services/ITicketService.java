package com.esprit.pi.services;

import com.esprit.pi.entities.Ticket;

import java.util.List;

public interface ITicketService {
    Ticket createTicket(Ticket ticket);
    Ticket updateTicket(Long id, Ticket ticket);
    void deleteTicket(Long id);
    Ticket getTicketById(Long id);
    List<Ticket> getAllTickets();
    List<Ticket> getTicketsByUserId(Long userId);
}

