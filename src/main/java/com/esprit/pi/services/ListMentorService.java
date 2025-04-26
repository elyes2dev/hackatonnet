package com.esprit.pi.services;

import com.esprit.pi.entities.ListMentor;
import com.esprit.pi.repositories.ListMentorRepository;
import com.esprit.pi.repositories.UserRepository;
import com.esprit.pi.repositories.IHackathonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class ListMentorService {

    @Autowired
    private ListMentorRepository listMentorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IHackathonRepository hackathonRepository;

    @Autowired
    private GoogleCalendarService googleCalendarService;

    // Create
    @Transactional
    public ListMentor createListMentor(Long userId, Long hackathonId, int numberOfTeams) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        var hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon not found"));

        ListMentor listMentor = new ListMentor();
        listMentor.setMentor(user);
        listMentor.setHackathon(hackathon);
        listMentor.setNumberOfTeams(numberOfTeams);

        ListMentor savedListMentor = listMentorRepository.save(listMentor);

        // Don't automatically add to calendar - let the user decide later
        return savedListMentor;
    }

    // Read
    public List<ListMentor> getAllListMentors() {
        return listMentorRepository.findAll();
    }

    public Optional<ListMentor> getListMentorById(Long id) {
        return listMentorRepository.findById(id);
    }

    public List<ListMentor> getListMentorsByMentorId(Long mentorId) {
        return listMentorRepository.findByMentorId(mentorId);
    }

    public List<ListMentor> getListMentorsByHackathonId(Long hackathonId) {
        return listMentorRepository.findByHackathonId(hackathonId);
    }

    // Update
    @Transactional
    public ListMentor updateListMentor(Long id, int numberOfTeams) {
        ListMentor listMentor = listMentorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mentor listing not found"));

        listMentor.setNumberOfTeams(numberOfTeams);
        return listMentorRepository.save(listMentor);
    }

    // Delete
    public void deleteListMentor(Long id) {
        listMentorRepository.deleteById(id);
    }

    // Add event to Google Calendar for a mentor listing
    public void addToGoogleCalendar(Long listMentorId) {
        ListMentor listMentor = listMentorRepository.findById(listMentorId)
                .orElseThrow(() -> new RuntimeException("Mentor listing not found"));

        try {
            if (googleCalendarService.hasValidToken(listMentor.getMentor())) {
                String eventTitle = "Hackathon: " + listMentor.getHackathon().getTitle();
                String eventDescription = "Mentoring " + listMentor.getNumberOfTeams() + " teams at " +
                        listMentor.getHackathon().getTitle();

                LocalDateTime startDateTime = listMentor.getHackathon().getStartDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                LocalDateTime endDateTime = listMentor.getHackathon().getEndDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                googleCalendarService.createEvent(
                        listMentor.getMentor(),
                        eventTitle,
                        eventDescription,
                        startDateTime,
                        endDateTime
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to add event to Google Calendar: " + e.getMessage(), e);
        }
    }
}