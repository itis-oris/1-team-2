package com.tourlist.service.impl;

import com.tourlist.dao.RoleDAO;
import com.tourlist.dao.impl.RoleDAOImpl;
import com.tourlist.model.Role;
import com.tourlist.service.RoleService;

import java.util.List;

public class RoleServiceImpl implements RoleService {

    private final RoleDAO roleDAO = new RoleDAOImpl();

    @Override
    public Role getRoleById(int id) {
        return roleDAO.findById(id);
    }

    @Override
    public Role getRoleByName(String name) {
        return roleDAO.findByName(name);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleDAO.findAll();
    }
}

