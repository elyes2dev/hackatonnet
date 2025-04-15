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
public class Hackathon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String location;
    private String logo;

    private int maxMembers;

    private Boolean isOnline;  // Assuming it's a boolean value indicating if it's online or not.

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @JsonIgnore // 🔥 This breaks the loop from Hackathon → User → Hackathon
    private User createdBy;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // Relationship with Post (One hackathon can have many posts)
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;  // Posts related to this hackathon


    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference // Prevent infinite recursion when serializing
    private List<Prize> prizes; // One Hackathon -> Multiple Prizes

    // Relationship with Team (One hackathon can have many teams)
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams;  // Teams participating in the hackathon
}