package com.esprit.pi.services;

import com.esprit.pi.entities.Skill;
import java.util.List;
import java.util.Optional;

public interface ISkillService {
    List<Skill> getAllSkills();
    Optional<Skill> getSkillById(Long id);
    Skill createSkill(Skill skill);
    Skill updateSkill(Long id, Skill skill);
    void deleteSkill(Long id);
}
