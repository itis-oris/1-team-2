package com.tourlist.dao.impl;


import com.tourlist.dao.RoleDAO;
import com.tourlist.model.Role;
import com.tourlist.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAOImpl implements RoleDAO {

    private static final Logger logger = LogManager.getLogger(RoleDAOImpl.class);

    @Override
    public Role findById(int id) {
        String sql = "SELECT * FROM roles WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractRole(rs);
            }
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("findById error", e);
        }
        return null;
    }

    @Override
    public Role findByName(String name) {
        String sql = "SELECT * FROM roles WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractRole(rs);
            }
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("findByName error", e);
        }
        return null;
    }

    @Override
    public List<Role> findAll() {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT * FROM roles";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(extractRole(rs));
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("findAll error", e);
        }
        return list;
    }

    private Role extractRole(ResultSet rs) throws SQLException {
        return new Role(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}

