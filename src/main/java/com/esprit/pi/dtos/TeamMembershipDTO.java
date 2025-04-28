package com.esprit.pi.dtos;

import com.esprit.pi.entities.TeamMembers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamMembershipDTO {
    private Long id;           // TeamMembers ID
    private Long teamId;       // Team ID
    private String teamName;   // Team name (optional)
    private Long userId;       // User ID

    // Constructors, getters, and setters
    public TeamMembershipDTO(TeamMembers member) {
        this.id = member.getId();
        this.teamId = member.getTeam().getId();
        this.teamName = member.getTeam().getTeamName();
        this.userId = member.getUser().getId();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
