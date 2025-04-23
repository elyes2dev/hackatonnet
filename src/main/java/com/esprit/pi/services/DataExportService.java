package com.esprit.pi.services;

import com.esprit.pi.entities.*;
import com.esprit.pi.repositories.ListMentorRepository;
import com.esprit.pi.repositories.MentorApplicationRepository;
import com.esprit.pi.repositories.TeamRepository;
import com.esprit.pi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataExportService {

    private final UserRepository userRepository;
    private final MentorApplicationRepository mentorApplicationRepository;
    private final ListMentorRepository listMentorRepository;
    private final TeamRepository teamRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public DataExportService(UserRepository userRepository,
                             MentorApplicationRepository mentorApplicationRepository,
                             ListMentorRepository listMentorRepository,
                             TeamRepository teamRepository,
                             RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.mentorApplicationRepository = mentorApplicationRepository;
        this.listMentorRepository = listMentorRepository;
        this.teamRepository = teamRepository;
        this.restTemplate = restTemplate;
    }

    public String getMentorRecommendations(Long teamId, Long hackathonId) throws Exception {
        // Fetch team data
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        List<TeamMembers> members = team.getTeamMembers().stream()
                .filter(tm -> !tm.getRole().equals(TeamMembers.Role.MENTOR))
                .collect(Collectors.toList());

        List<Map<String, Object>> memberData = members.stream().map(tm -> {
            User user = tm.getUser();
            Map<String, Object> member = new HashMap<>();
            member.put("id", user.getId().toString());
            member.put("bio", user.getDescription() != null ? user.getDescription() : "");
            member.put("skills", user.getSkills().stream()
                    .map(Skill::getName)
                    .collect(Collectors.toList()));
            return member;
        }).collect(Collectors.toList());

        // Fetch mentor data
        List<ListMentor> listMentors = listMentorRepository.findByHackathonId(hackathonId);
        List<User> mentors = listMentors.stream()
                .map(ListMentor::getMentor)
                .collect(Collectors.toList());

        List<String> availableMentorIds = mentors.stream()
                .map(user -> user.getId().toString())
                .collect(Collectors.toList());

        List<Map<String, Object>> mentorData = mentors.stream().map(mentor -> {
            MentorApplication application = mentorApplicationRepository.findByUser(mentor).orElse(null);
            String bio = mentor.getDescription() != null ? mentor.getDescription() : "";
            if (application != null) {
                bio += " " + (application.getApplicationText() != null ? application.getApplicationText() : "");
                if (application.getPreviousExperiences() != null) {
                    bio += " " + application.getPreviousExperiences().stream()
                            .map(PreviousExperience::getDescription)
                            .collect(Collectors.joining(" "));
                }
            }
            int experienceYears = application != null && application.getPreviousExperiences() != null ?
                    application.getPreviousExperiences().size() : 0;
            double rating = mentor.getEvaluations() != null ?
                    mentor.getEvaluations().stream().mapToDouble(MentorEvaluation::getRating).average().orElse(0.6) : 0.6;

            Map<String, Object> mentorMap = new HashMap<>();
            mentorMap.put("id", mentor.getId().toString());
            mentorMap.put("bio", bio);
            mentorMap.put("experience_years", experienceYears);
            mentorMap.put("rating", rating);
            mentorMap.put("user_id", mentor.getId().toString());
            mentorMap.put("skills", mentor.getSkills().stream()
                    .map(Skill::getName)
                    .collect(Collectors.toList()));
            mentorMap.put("name", mentor.getName() + " " + (mentor.getLastname() != null ? mentor.getLastname() : ""));
            return mentorMap;
        }).collect(Collectors.toList());

        // Prepare users data
        List<Map<String, Object>> userData = mentors.stream().map(mentor -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", mentor.getId().toString());
            userMap.put("name", mentor.getName() + " " + (mentor.getLastname() != null ? mentor.getLastname() : ""));
            return userMap;
        }).collect(Collectors.toList());

        // Prepare skills/tags data
        Set<Skill> allSkills = mentors.stream()
                .flatMap(mentor -> mentor.getSkills().stream())
                .collect(Collectors.toSet());
        List<Map<String, Object>> tagData = allSkills.stream().map(skill -> {
            Map<String, Object> tagMap = new HashMap<>();
            tagMap.put("tag_id", skill.getId().toString());
            tagMap.put("tag_name", skill.getName());
            return tagMap;
        }).collect(Collectors.toList());

        // Prepare mentor_tags data
        List<Map<String, Object>> mentorTagData = mentors.stream().flatMap(mentor ->
                mentor.getSkills().stream().map(skill -> {
                    Map<String, Object> tagMap = new HashMap<>();
                    tagMap.put("mentor_id", mentor.getId().toString());
                    tagMap.put("tag_id", skill.getId().toString());
                    return tagMap;
                })
        ).collect(Collectors.toList());

        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("teamId", teamId.toString());
        requestBody.put("hackathonId", hackathonId.toString());
        requestBody.put("members", memberData);
        requestBody.put("availableMentorIds", availableMentorIds);
        requestBody.put("numRecommendations", 3);
        requestBody.put("mentorData", new HashMap<String, Object>() {{
            put("mentors", mentorData);
            put("users", userData);
            put("tags", tagData);
            put("mentor_tags", mentorTagData);
        }});

        // Send request to Flask API
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(
                "http://localhost:5000/recommend_mentors",
                entity,
                String.class
        );
    }
}