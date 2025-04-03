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

    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //private Long id_document;

    //private String path;

    //private TypeDocument typedocument;

    //@ManyToOne
    // private Resources resource;

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;
        private String filePath;
        private String fileType; // pdf, zip, docx, etc.
        private Long fileSize;

        @Enumerated(EnumType.STRING)
        private TypeDocument type; // SLIDE, EXERCISE, SOLUTION, etc.

        @ManyToOne
        @JoinColumn(name = "resource_id")
        private Resources resource;

        public Resources getResource() {
            return resource;
        }

        public void setResource(Resources resource) {
            this.resource = resource;
        }

        public TypeDocument getType() {
            return type;
        }

        public void setType(TypeDocument type) {
            this.type = type;
        }

        public Long getFileSize() {
            return fileSize;
        }

        public void setFileSize(Long fileSize) {
            this.fileSize = fileSize;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
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

        // Getters and setters
    }
