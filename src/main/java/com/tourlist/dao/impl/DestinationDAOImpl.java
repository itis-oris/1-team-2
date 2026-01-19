package com.tourlist.dao.impl;


import com.tourlist.dao.DestinationDAO;
import com.tourlist.model.Destination;
import com.tourlist.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DestinationDAOImpl implements DestinationDAO {

    private static final Logger logger = LogManager.getLogger(DestinationDAOImpl.class);

    @Override
    public Destination findById(int id) {
        String sql = "SELECT * FROM destinations WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractDestination(rs);
            }
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("findById error", e);
        }
        return null;
    }

    @Override
    public List<Destination> findAll() {
        List<Destination> list = new ArrayList<>();
        String sql = "SELECT * FROM destinations";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(extractDestination(rs));
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("findAll error", e);
        }
        return list;
    }

    @Override
    public boolean create(Destination destination) {
        String sql = "INSERT INTO destinations (name, country) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, destination.getName());
            ps.setString(2, destination.getCountry());
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("create error", e);
        }
        return false;
    }

    @Override
    public boolean update(Destination destination) {
        String sql = "UPDATE destinations SET name=?, country=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, destination.getName());
            ps.setString(2, destination.getCountry());
            ps.setInt(3, destination.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("update error", e);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM destinations WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("delete error", e);
        }
        return false;
    }
    @Override
    public List<Destination> findByNameContaining(String namePart, int limit) {
        String sql = "SELECT id, name, country FROM destinations WHERE LOWER(name) LIKE LOWER(?) ORDER BY name LIMIT ?";
        List<Destination> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + namePart + "%");
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(extractDestination(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("findByDestination error", e);
        }
        return list;
    }

    private Destination extractDestination(ResultSet rs) throws SQLException {
        return new Destination(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("country")
        );
    }
}

