package com.normalizedb.security.services;

import com.normalizedb.security.entities.AuthorizedRole;
import com.normalizedb.security.entities.application.Role;
import com.normalizedb.security.repositories.RoleRepository;
import com.normalizedb.security.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RolesService {
    private final String DELETE_PREFIX = "DELETED_";
    private RoleRepository roleRepository;
    private UserRepository userRepository;

    public RolesService(RoleRepository roleRepository,
                        UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public void persistRoles() {
        List<AuthorizedRole> fetchedRoles = roleRepository.findAll();
        List<AuthorizedRole> toDelete = fetchedRoles
                                            .stream()
                                            .filter((AuthorizedRole existingRole) -> {
                                                if(existingRole.getName().startsWith(DELETE_PREFIX)) {
                                                    return false;
                                                }
                                                for(Role currentRole: Role.values()) {
                                                    if(existingRole.getName().equals(currentRole.getRole())) {
                                                        return false;
                                                    }
                                                }
                                                return true;
                                            }).collect(Collectors.toList());
        //We are unable to delete roles because of existing users that employ these deleted roles.
        //SOLUTION: Update the role name to include a deletion prefix that indicates that users of such a role,
        //no longer have permissions to any services
        for(AuthorizedRole deleteRole: toDelete) {
            String updatedRoleName = DELETE_PREFIX.concat(deleteRole.getName());
            boolean alreadyExists = fetchedRoles.stream().anyMatch((AuthorizedRole exists) -> exists.getName().equals(updatedRoleName));
            if(!alreadyExists){
                roleRepository.save(new AuthorizedRole(updatedRoleName));
                roleRepository.flush();
            }
            userRepository.updateRole(deleteRole.getName(), updatedRoleName);
            roleRepository.delete(deleteRole);
        }
        //Persist all roles defined in the application layer
        for(Role newRole: Role.values()) {
            AuthorizedRole toSave = new AuthorizedRole(newRole.getRole());
            if(fetchedRoles.stream().noneMatch((AuthorizedRole exists) -> newRole.getRole().equals(exists.getName()))){
                roleRepository.save(toSave);
                roleRepository.flush();
                String deletedName = DELETE_PREFIX.concat(newRole.getRole());
                boolean previouslyDeleted = fetchedRoles.stream().anyMatch((AuthorizedRole exists) -> exists.getName().equals(deletedName));
                if(previouslyDeleted) {
                    userRepository.updateRole(deletedName, newRole.getRole());
                    roleRepository.delete(deletedName);
                }
            }
        }
    }
}
