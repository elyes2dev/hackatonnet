package com.esprit.pi.repositories;

import com.esprit.pi.entities.Team;
import com.esprit.pi.entities.TeamMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Optional<TeamMembers> findByUserIdAndTeamId(Long userId, Long teamId);

    /**
     * Count how many teams a user is mentoring in a specific hackathon
     *
     * @param userId The ID of the user (mentor)
     * @param role The role (should be MENTOR)
     * @param hackathonId The ID of the hackathon
     * @return Count of teams the user is mentoring
     */
    @Query("SELECT COUNT(tm) FROM TeamMembers tm " +
            "WHERE tm.user.id = :userId " +
            "AND tm.role = :role " +
            "AND tm.team.hackathon.id = :hackathonId")
    int countByUserIdAndRoleAndTeamHackathonId(
            @Param("userId") Long userId,
            @Param("role") TeamMembers.Role role,
            @Param("hackathonId") Long hackathonId
    );

    List<TeamMembers> findAllByUserId(Long userId);


}