package com.esprit.pi.repositories;

import com.esprit.pi.entities.TeamDiscussion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamDiscussionRepository extends JpaRepository<TeamDiscussion, Long> {
    List<TeamDiscussion> findByTeamIdOrderByCreatedAtDesc(Long teamId);
    List<TeamDiscussion> findByTeamMemberIdOrderByCreatedAtDesc(Long teamMemberId);
    Long countByTeamIdAndIsReadFalse(Long teamId);
}