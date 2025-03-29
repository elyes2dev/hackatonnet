package com.esprit.pi.entities;

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

    // Many-to-one relationship with Team
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // Many-to-one relationship with User
    @ManyToOne
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
    private List<TeamDiscussion> teamDiscussions;

    @OneToOne(mappedBy = "teamMember", cascade = CascadeType.ALL)
    private TeamSubmission teamSubmission;  // One-to-one relationship with TeamSubmission

    public enum Role {
        LEADER,
        MEMBER,
        MENTOR
    }
}
