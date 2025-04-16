package com.esprit.pi.entities;

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
@NoArgsConstructor
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
    private List<Prize> prizes;

    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams;

    public User getCreatedBy() {
        return this.createdBy;
    }

    public Long getId() {
        return this.id;
    }

}