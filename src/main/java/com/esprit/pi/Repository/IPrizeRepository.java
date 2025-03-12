package com.esprit.pi.Repository;

import com.esprit.pi.entities.Prize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPrizeRepository extends JpaRepository<Prize,Long> {
    List<Prize> findByHackathonId(long hackathonId);
    List<Prize> findBySponsorId(long sponsorId);
}
