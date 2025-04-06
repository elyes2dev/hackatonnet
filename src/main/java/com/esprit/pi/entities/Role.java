package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Role {
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "roles")
  //  @JsonManagedReference  // Prevents recursion when serializing the User entity
//    @JsonBackReference  // Prevent recursion when serializing the Role entity (back reference)
    @JsonIgnoreProperties("roles")  // Ignore the 'roles' field in User to prevent recursion

    private Set<User> users;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
