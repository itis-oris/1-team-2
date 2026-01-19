package com.tourlist.dao;


import com.tourlist.model.Role;

import java.util.List;

public interface RoleDAO {
    Role findById(int id);
    Role findByName(String name);
    List<Role> findAll();
}