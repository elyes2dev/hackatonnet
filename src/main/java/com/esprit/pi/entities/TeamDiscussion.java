package com.esprit.pi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TeamDiscussion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-one relationship with TeamMember (Each discussion is by one team member)
    @ManyToOne
    @JoinColumn(name = "team_member_id", nullable = false)
    private TeamMembers teamMember;

    // Many-to-one relationship with User (Each discussion is posted by a user)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The message of the discussion
    private String message;

    // Timestamp when the message was created
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
