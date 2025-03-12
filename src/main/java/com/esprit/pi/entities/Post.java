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
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Post implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("logo")
    private String logo;

    @ManyToOne
    @JoinColumn(name = "posted_by", nullable = false)
    @JsonProperty("postedBy")
    private User postedBy;

    @ManyToOne
    @JoinColumn(name = "hackathon_id", nullable = false)
    @JsonProperty("hackathon")
    private Hackathon hackathon;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("createdAt")
    private Date createdAt;

    public Hackathon getHackathon() {
        return hackathon;
    }
    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
    }
}