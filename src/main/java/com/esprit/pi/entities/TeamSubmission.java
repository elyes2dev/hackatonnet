package com.esprit.pi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TeamSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "team_member_id", referencedColumnName = "id", nullable = false)
    private TeamMembers teamMember;  // Foreign key to TeamMember (One-to-one relationship)

    private String projectName;
    private String description;
    private String repoLink;

    @Temporal(TemporalType.TIMESTAMP)
    private Date submissionDate;

}
