package com.esprit.pi.controllers;

import com.esprit.pi.entities.ListMonitor;
import com.esprit.pi.services.ListMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/list-monitors")
public class ListMonitorController {

    @Autowired
    private ListMonitorService listMonitorService;

    // Create
    @PostMapping
    public ResponseEntity<ListMonitor> createListMonitor(@RequestBody ListMonitor listMonitor) {
        ListMonitor created = listMonitorService.createListMonitor(listMonitor);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Read
    @GetMapping
    public ResponseEntity<List<ListMonitor>> getAllListMonitors() {
        List<ListMonitor> listMonitors = listMonitorService.getAllListMonitors();
        return new ResponseEntity<>(listMonitors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListMonitor> getListMonitorById(@PathVariable Long id) {
        Optional<ListMonitor> listMonitor = listMonitorService.getListMonitorById(id);
        return listMonitor.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/monitor/{monitorId}")
    public ResponseEntity<List<ListMonitor>> getListMonitorsByMonitorId(@PathVariable Long monitorId) {
        List<ListMonitor> listMonitors = listMonitorService.getListMonitorsByMonitorId(monitorId);
        return new ResponseEntity<>(listMonitors, HttpStatus.OK);
    }

    @GetMapping("/hackathon/{hackathonId}")
    public ResponseEntity<List<ListMonitor>> getListMonitorsByHackathonId(@PathVariable Long hackathonId) {
        List<ListMonitor> listMonitors = listMonitorService.getListMonitorsByHackathonId(hackathonId);
        return new ResponseEntity<>(listMonitors, HttpStatus.OK);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<ListMonitor> updateListMonitor(@PathVariable Long id, @RequestBody ListMonitor listMonitor) {
        Optional<ListMonitor> existingListMonitor = listMonitorService.getListMonitorById(id);
        if (existingListMonitor.isPresent()) {
            listMonitor.setId(id);
            ListMonitor updated = listMonitorService.updateListMonitor(listMonitor);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListMonitor(@PathVariable Long id) {
        Optional<ListMonitor> existingListMonitor = listMonitorService.getListMonitorById(id);
        if (existingListMonitor.isPresent()) {
            listMonitorService.deleteListMonitor(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}