package com.esprit.pi.services;

import com.esprit.pi.dtos.MentorRecommendationDTO;
import com.esprit.pi.entities.ListMentor;
import com.esprit.pi.entities.MentorRecommendation;
import com.esprit.pi.entities.Team;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.ListMentorRepository;
import com.esprit.pi.repositories.TeamRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorMatchingService {

    private final RestTemplate restTemplate;
    private final TeamRepository teamRepository;
    private final ListMentorRepository listMentorRepository;
    private final ObjectMapper objectMapper;

    public List<MentorRecommendationDTO> getRecommendedMentorsForTeam(Long teamId, int count) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with ID: " + teamId));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("teamId", team.getId());
        requestBody.put("hackathonId", team.getHackathon().getId());
        requestBody.put("count", count);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            MentorRecommendationDTO[] recommendations = restTemplate.postForObject(
                    "http://localhost:5000/recommend_mentors",
                    new HttpEntity<>(requestBody, headers),
                    MentorRecommendationDTO[].class
            );
            return Arrays.asList(recommendations);
        } catch (RestClientException e) {
            throw new RuntimeException("Error calling mentor recommendation service", e);
        }
    }

    /**
     * Get all available mentors for a specific hackathon
     * @param hackathonId The ID of the hackathon
     * @return List of mentor users
     */
    public List<User> getAvailableMentorsForHackathon(Long hackathonId) {
        // Get mentors from ListMentorRepository
        List<ListMentor> listMentors = listMentorRepository.findByHackathonId(hackathonId);

        // Extract User objects from ListMentor entities
        return listMentors.stream()
                .map(ListMentor::getMentor)
                .collect(Collectors.toList());
    }
}