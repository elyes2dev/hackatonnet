package com.esprit.pi.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private skillEnum level;

    @ManyToMany(mappedBy = "skills") // mappedBy indicates the owning side of the relationship
    private Set<User> users;

    public String getName() {
        return name;
    }

    public skillEnum getLevel() {
        return level;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(skillEnum level) {
        this.level = level;
    }
}
