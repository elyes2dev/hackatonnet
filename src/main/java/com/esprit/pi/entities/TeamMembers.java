package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TeamMembers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id", nullable = false)
    @JsonBackReference
    private Team team;

    // Many-to-one relationship with User
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date joinedAt;  // Timestamp when the user joined the team

    // Role of the user in the team (LEADER, MEMBER, MENTOR)
    @Enumerated(EnumType.STRING)
    private Role role;  // Enum field for role (LEADER, MEMBER, MENTOR)

    // One-to-many relationship with TeamDiscussion (Each team member can post many discussions)
    @OneToMany(mappedBy = "teamMember", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevents serialization of teamDiscussions to break the loop
    private List<TeamDiscussion> teamDiscussions;

    @OneToOne(mappedBy = "teamMember", cascade = CascadeType.ALL)
    private TeamSubmission teamSubmission;  // One-to-one relationship with TeamSubmission

    public enum Role {
        LEADER,
        MEMBER,
        MENTOR
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Date getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Date joinedAt) { this.joinedAt = joinedAt; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public List<TeamDiscussion> getTeamDiscussions() { return teamDiscussions; }
    public void setTeamDiscussions(List<TeamDiscussion> teamDiscussions) { this.teamDiscussions = teamDiscussions; }
}