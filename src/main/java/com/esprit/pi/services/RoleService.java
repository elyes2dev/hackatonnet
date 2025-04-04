package com.esprit.pi.services;

import com.esprit.pi.entities.Role;
import com.esprit.pi.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService implements IRoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(Long id, Role role) {
        return roleRepository.findById(id).map(existingRole -> {
            existingRole.setName(role.getName());
            return roleRepository.save(existingRole);
        }).orElse(null);
    }


    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}