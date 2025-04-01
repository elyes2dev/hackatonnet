package com.esprit.pi.services;

import com.esprit.pi.entities.Skill;
import com.esprit.pi.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SkillService implements ISkillService {

    @Autowired
    private SkillRepository skillRepository;

    @Override
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    @Override
    public Optional<Skill> getSkillById(Long id) {
        return skillRepository.findById(id);
    }

    @Override
    public Skill createSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    @Override
    public Skill updateSkill(Long id, Skill skill) {
        return skillRepository.findById(id).map(existingSkill -> {
            existingSkill.setName(skill.getName());
            existingSkill.setLevel(skill.getLevel());
            return skillRepository.save(existingSkill);
        }).orElse(null);
    }


    @Override
    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }
}
