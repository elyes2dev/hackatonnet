package com.esprit.pi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "image_model")
public class ImageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_image;

    private String path;

    private String type;

    @Column(length = 5000000)
    private byte[] picByte;

    public ImageModel(String path, String type, byte[] picByte) {
        this.path = path;
        this.type = type;
        this.picByte = picByte;
    }
}