package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teamName;
    private String teamCode;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "is_full")
    private Boolean isFull;

    @Temporal(TemporalType.TIMESTAMP)
    private Date joinCodeExpirationTime;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathon hackathon;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<TeamMembers> teamMembers;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevents serialization of teamDiscussions
    private List<TeamDiscussion> teamDiscussions;

    // Getters and Setters (unchanged)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getTeamCode() { return teamCode; }
    public void setTeamCode(String teamCode) { this.teamCode = teamCode; }
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    public Boolean getIsFull() { return isFull; }
    public void setIsFull(Boolean isFull) { this.isFull = isFull; }
    public Date getJoinCodeExpirationTime() { return joinCodeExpirationTime; }
    public void setJoinCodeExpirationTime(Date joinCodeExpirationTime) { this.joinCodeExpirationTime = joinCodeExpirationTime; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Hackathon getHackathon() { return hackathon; }
    public void setHackathon(Hackathon hackathon) { this.hackathon = hackathon; }
    public List<TeamMembers> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(List<TeamMembers> teamMembers) { this.teamMembers = teamMembers; }
    public List<TeamDiscussion> getTeamDiscussions() { return teamDiscussions; }
    public void setTeamDiscussions(List<TeamDiscussion> teamDiscussions) { this.teamDiscussions = teamDiscussions; }
}