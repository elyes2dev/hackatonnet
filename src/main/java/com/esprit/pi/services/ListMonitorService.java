package com.esprit.pi.services;

import com.esprit.pi.entities.ListMonitor;
import com.esprit.pi.repositories.ListMonitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ListMonitorService {

    @Autowired
    private ListMonitorRepository listMonitorRepository;

    // Create
    public ListMonitor createListMonitor(ListMonitor listMonitor) {
        return listMonitorRepository.save(listMonitor);
    }

    // Read
    public List<ListMonitor> getAllListMonitors() {
        return listMonitorRepository.findAll();
    }

    public Optional<ListMonitor> getListMonitorById(Long id) {
        return listMonitorRepository.findById(id);
    }

    public List<ListMonitor> getListMonitorsByMonitorId(Long monitorId) {
        return listMonitorRepository.findByMonitorId(monitorId);
    }

    public List<ListMonitor> getListMonitorsByHackathonId(Long hackathonId) {
        return listMonitorRepository.findByHackathonId(hackathonId);
    }

    // Update
    public ListMonitor updateListMonitor(ListMonitor listMonitor) {
        return listMonitorRepository.save(listMonitor);
    }

    // Delete
    public void deleteListMonitor(Long id) {
        listMonitorRepository.deleteById(id);
    }
}

