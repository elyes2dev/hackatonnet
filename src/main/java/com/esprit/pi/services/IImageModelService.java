package com.esprit.pi.services;

import com.esprit.pi.entities.ImageModel;

import java.util.List;
import java.util.Optional;

public interface IImageModelService {
    ImageModel getImageById(Long id);

}
