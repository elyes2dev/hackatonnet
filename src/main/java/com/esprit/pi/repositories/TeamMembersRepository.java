package com.esprit.pi.repositories;

import com.esprit.pi.entities.Team;
import com.esprit.pi.entities.TeamMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMembersRepository extends JpaRepository<TeamMembers, Long> {
    Optional<TeamMembers> findByUserIdAndTeamHackathonId(Long userId, Long hackathonId);
    Optional<TeamMembers> findByUserId(Long userId);
    List<TeamMembers> findByTeamId(Long teamId);
    Optional<TeamMembers> findFirstByTeamAndRoleNot(Team team, TeamMembers.Role role);
    Long countByTeamId(Long teamId);

    Optional<TeamMembers> findByUserIdAndTeamId(Long userId, Long teamId);}