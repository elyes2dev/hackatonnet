package com.esprit.pi.entities;

import jakarta.annotation.Resource;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.w3c.dom.DocumentType;

import java.lang.reflect.Type;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_document;

    private String path;

    private TypeDocument typedocument;

    @ManyToOne
    private Resources resource;

        // Getters and setters
    }
