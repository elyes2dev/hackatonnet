package com.esprit.pi.repositories;

import com.esprit.pi.entities.ListMonitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListMonitorRepository extends JpaRepository<ListMonitor, Long> {
    List<ListMonitor> findByMonitorId(Long monitorId);
    List<ListMonitor> findByHackathonId(Long hackathonId);
}
