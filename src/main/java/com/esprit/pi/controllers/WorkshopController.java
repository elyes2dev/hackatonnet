package com.esprit.pi.controllers;


import com.esprit.pi.entities.Workshop;
import com.esprit.pi.services.IWorkshopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/workshops")
public class WorkshopController {

    @Autowired
    private IWorkshopService workshopService;

    @PostMapping("/add")
    public ResponseEntity<Workshop> addWorkshop(@RequestBody Workshop workshop) {
        Workshop savedWorkshop = workshopService.addWorkshop(workshop);
        return ResponseEntity.ok(savedWorkshop);
    }

    @GetMapping("/{id}")
    public Optional<Workshop> getWorkshopById(@PathVariable Long id) {
        return workshopService.findById(id);
    }

    @GetMapping
    public List<Workshop> getAllWorkshops() {
        return workshopService.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteWorkshop(@PathVariable Long id) {
        workshopService.deleteById(id);
    }
}

