package com.esprit.pi.repositories;


import com.esprit.pi.entities.TeamDiscussion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamDiscussionRepository extends JpaRepository<TeamDiscussion, Long> {
}