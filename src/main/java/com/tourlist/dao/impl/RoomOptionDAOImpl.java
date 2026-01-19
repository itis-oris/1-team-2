package com.tourlist.dao.impl;

import com.tourlist.dao.RoomOptionDAO;
import com.tourlist.model.RoomOption;
import com.tourlist.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomOptionDAOImpl implements RoomOptionDAO {

    private static final Logger logger = LogManager.getLogger(RoomOptionDAOImpl.class);
    @Override
    public List<RoomOption> findAllRoomsByTourId(int tour_id) {
        List<RoomOption> list = new ArrayList<>();

        String roomSql = """
            SELECT ro.id, ro.room_type, ro.capacity, ro.bed_type, ro.bed_count, ro.price_multiplier
            FROM room_options ro
            JOIN tour_room_options tro ON ro.id = tro.room_option_id
            WHERE tro.tour_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(roomSql)) {

            ps.setInt(1, tour_id); // ← обязательно установить параметр!

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractRoomOption(rs));
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            logger.error("findAllRoomsByTourId error", e);
        }

        return list;
    }


    @Override
    public RoomOption findById(int id) {
        String sql = "SELECT * FROM room_options WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractRoomOption(rs);
            }
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("findById error", e);
        }
        return null;
    }

    @Override
    public List<RoomOption> findAll() {
        List<RoomOption> list = new ArrayList<>();
        String sql = "SELECT * FROM room_options";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(extractRoomOption(rs));
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("findAll error", e);
        }
        return list;
    }

    @Override
    public boolean create(RoomOption option) {
        String sql = "INSERT INTO room_options (room_type, capacity, bed_type, bed_count, price_multiplier) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, option.getRoomType());
            ps.setInt(2, option.getCapacity());
            ps.setString(3, option.getBed_type());
            ps.setInt(4, option.getBed_count());
            ps.setDouble(5, option.getPriceMultiplier());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        option.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("create error", e);
        }
        return false;
    }

    @Override
    public boolean update(RoomOption option) {
        String sql = "UPDATE room_options SET room_type=?, capacity=?, bed_type=?, bed_count=?, price_multiplier=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, option.getRoomType());
            ps.setInt(2, option.getCapacity());
            ps.setString(3, option.getBed_type());
            ps.setInt(4, option.getBed_count());
            ps.setDouble(5, option.getPriceMultiplier());
            ps.setInt(6, option.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("update error", e);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM room_options WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("delete error", e);
        }
        return false;
    }

    private RoomOption extractRoomOption(ResultSet rs) throws SQLException {
        return new RoomOption(
                rs.getInt("id"),
                rs.getString("room_type"),
                rs.getInt("capacity"),
                rs.getString("bed_type"),
                rs.getInt("bed_count"),
                rs.getDouble("price_multiplier")
        );
    }

}

