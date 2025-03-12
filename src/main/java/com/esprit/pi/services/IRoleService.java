package com.esprit.pi.services;

import com.esprit.pi.entities.Role;
import java.util.List;
import java.util.Optional;

public interface IRoleService {
    List<Role> getAllRoles();
    Optional<Role> getRoleById(Long id);
    Role createRole(Role role);
    Role updateRole(Long id, Role role);
    void deleteRole(Long id);
}
