package com.esprit.pi.repositories;

import com.esprit.pi.entities.TeamMembers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMembersRepository extends JpaRepository<TeamMembers, Long>
{
}
