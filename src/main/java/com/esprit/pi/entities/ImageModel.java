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
    //@Id
   // @GeneratedValue(strategy = GenerationType.IDENTITY)
   // private Long id_image;

   // private String path;

  //  private String type;

  //  @Column(length = 5000000)
  //  private byte[] picByte;

  //  public ImageModel(String path, String type, byte[] picByte) {
   //     this.path = path;
   //     this.type = type;
   //     this.picByte = picByte;
   // }
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

    private String name;
    private String filePath;
    private String fileType;
    private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resources resource;

    // Add setResource method
    public void setResource(Resources resource) {
        this.resource = resource;
    }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public Resources getResource() {
    return resource;
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

  // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}