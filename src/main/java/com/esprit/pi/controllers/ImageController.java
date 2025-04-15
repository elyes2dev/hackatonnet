package com.esprit.pi.controllers;

import com.esprit.pi.entities.ImageModel;
import com.esprit.pi.services.IImageModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private IImageModelService imageService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getImage(@PathVariable("id") Long id) {
        ImageModel image = imageService.getImageById(id);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(image.getPicByte());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, image.getType());
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(bis));
    }
}
