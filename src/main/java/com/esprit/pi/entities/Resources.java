package com.esprit.pi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String image;
    @ManyToOne
    @JoinColumn(name = "workshop_id", nullable = false)
    private Workshop workshop;

 //   @OneToMany(mappedBy = "resource")
 //   private List<Document> documents;

   // @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
   // @JoinTable(name = "resource_images",
     //       joinColumns = {
     //               @JoinColumn(name = "id_resources")
     //       },
    //        inverseJoinColumns = {
    //                @JoinColumn(name = "id_image")
   //         }
   // )
  //  private Set<ImageModel> resourceImages;
}
