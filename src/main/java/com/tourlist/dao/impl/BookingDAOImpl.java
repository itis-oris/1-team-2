package com.tourlist.dao.impl;


import com.tourlist.dao.BookingDAO;
import com.tourlist.model.Booking;
import com.tourlist.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAOImpl implements BookingDAO {

    private static final Logger logger = LogManager.getLogger(BookingDAOImpl.class);

    @Override
    public Booking findById(int id) {
        String sql = "SELECT * FROM bookings WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractBooking(rs);
            }
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("findById error", e);
        }
        return null;
    }

    @Override
    public List<Booking> findByUserId(int userId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(extractBooking(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("findByUserId error", e);
        }
        return list;
    }

    @Override
    public boolean create(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, tour_id, booking_date, people_count, rooms_count, total_price, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, booking.getUserId());
            ps.setInt(2, booking.getTourId());
            ps.setTimestamp(3, Timestamp.valueOf(booking.getBookingDate()));
            ps.setInt(4, booking.getPeopleCount());
            ps.setInt(5, booking.getRoomsCount());
            ps.setDouble(6, booking.getTotalPrice());
            ps.setString(7, booking.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("create error", e);
        }
        return false;
    }

    @Override
    public boolean update(Booking booking) {
        String sql = "UPDATE bookings SET user_id=?, tour_id=?, booking_date=?, people_count=?, rooms_count=?, total_price=?, status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, booking.getUserId());
            ps.setInt(2, booking.getTourId());
            ps.setTimestamp(3, Timestamp.valueOf(booking.getBookingDate()));
            ps.setInt(4, booking.getPeopleCount());
            ps.setInt(5, booking.getRoomsCount());
            ps.setDouble(6, booking.getTotalPrice());
            ps.setString(7, booking.getStatus());
            ps.setInt(8, booking.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("update error", e);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM bookings WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("delete error", e);
        }
        return false;
    }

    private Booking extractBooking(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getInt("id"),
                rs.getInt("user_id"),
                null, // user загружается отдельным DAO
                rs.getInt("tour_id"),
                null, // tour загружается отдельным DAO
                rs.getTimestamp("booking_date").toLocalDateTime(),
                rs.getInt("people_count"),
                rs.getInt("rooms_count"),
                rs.getDouble("total_price"),
                rs.getString("status")
        );
    }
}
