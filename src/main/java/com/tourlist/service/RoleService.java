package com.tourlist.service;

import com.tourlist.model.Role;
import java.util.List;

public interface RoleService {
    Role getRoleById(int id);
    Role getRoleByName(String name);
    List<Role> getAllRoles();
}
