package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor

public class Hackathon implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("location")
    private String location;

    @JsonProperty("logo")
    private String logo;

    @JsonProperty("maxMembers")
    private int maxMembers;

    @JsonProperty("isOnline")
    private Boolean isOnline;
    private Integer maxTeamSize; // Maximum number of members allowed in a team

    @JsonProperty("description")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("startDate")
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("endDate")
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
//    @JsonIgnore // 🔥 This breaks the loop from Hackathon → User → Hackathon
    @JsonProperty("createdBy")
    private User createdBy;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("createdAt")
    private Date createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference // Prevent infinite recursion when serializing
    private List<Prize> prizes; // One Hackathon -> Multiple Prizes

    // Relationship with Team (One hackathon can have many teams)
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams;  // Teams participating in the hackathon

    public User getCreatedBy() {
        return this.createdBy;
    }


    public Hackathon(int i, String s, int i1) {

    }
    // Default constructor required by JPA
    public Hackathon() {
    }

    // Your other constructors, getters, and setters
    public Hackathon(Long id, String title, Integer maxTeamSize) {
        this.id = id;
        this.title = title;
        this.maxTeamSize = maxTeamSize;
    }

    // Explicitly add the methods that are causing the error
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMaxTeamSize() {
        return this.maxTeamSize;
    }

    public void setMaxTeamSize(Integer maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }
}