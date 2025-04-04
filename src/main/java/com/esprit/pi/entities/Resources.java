package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Resources {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private skillEnum niveau;
  //  private String image;
    @ManyToOne
    @JoinColumn(name = "workshop_id", nullable = false)
    @JsonBackReference
    private Workshop workshop;

    @OneToMany(mappedBy = "resource")
    private List<Document> documents;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "resource_images",
            joinColumns = {
                    @JoinColumn(name = "id_resources")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "id_image")
            }
    )
    private Set<ImageModel> resourceImages;



    // Helper methods

    public Set<ImageModel> getResourceImages() {
        return resourceImages;
    }

    public void setResourceImages(Set<ImageModel> resourceImages) {
        this.resourceImages = resourceImages;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }


    public skillEnum getNiveau() {
        return niveau;
    }

    public void setNiveau(skillEnum niveau) {
        this.niveau = niveau;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}