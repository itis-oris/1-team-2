package com.tourlist.dao.impl;

import com.tourlist.dao.TourDAO;
import com.tourlist.model.RoomOption;
import com.tourlist.model.Tour;
import com.tourlist.model.User;
import com.tourlist.util.DBConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TourDAOImpl implements TourDAO {

    private static final Logger logger = LogManager.getLogger(TourDAOImpl.class);


    @Override
    public List<Tour> search(String query, String startDate, String destinationId, int limit, int offset) {
        List<Tour> tours = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM tours WHERE 1=1");

        // динамически собираем SQL в зависимости от фильтров
        if (query != null && !query.isEmpty()) {
            sql.append(" AND LOWER(title) LIKE LOWER(?)");
        }
        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND start_date >= ?");
        }
        if (destinationId != null && !destinationId.isEmpty()) {
            sql.append(" AND destination_id = ?");
        }

        sql.append(" ORDER BY start_date ASC LIMIT ? OFFSET ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (query != null && !query.isEmpty()) {
                ps.setString(paramIndex++, "%" + query + "%");
            }
            if (startDate != null && !startDate.isEmpty()) {
                ps.setDate(paramIndex++, Date.valueOf(startDate));
            }
            if (destinationId != null && !destinationId.isEmpty()) {
                ps.setInt(paramIndex++, Integer.parseInt(destinationId));
            }


            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tours.add(extractTour(rs));
                }
            }
            logger.info("SQL: " + sql);
            logger.info("Params: query=" + query + ", startDate=" + startDate + ", destinationId=" + destinationId);
            logger.info("Found tours: " + tours.size());

        } catch (SQLException | ClassNotFoundException e) {
            logger.error("search tours error", e);
        }

        return tours;
    }

    @Override
    public Tour findById(int id) {
        String sql = "SELECT * FROM tours WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractTour(rs);
            }
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("findById error", e);
        }
        return null;
    }

    @Override
    public List<Tour> findAll() {
        List<Tour> list = new ArrayList<>();
        String sql = "SELECT * FROM tours";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(extractTour(rs));
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("findAll error", e);
        }
        return list;
    }

    @Override
    public List<Tour> findByDestination(int destinationId) {
        List<Tour> list = new ArrayList<>();
        String sql = "SELECT * FROM tours WHERE destination_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, destinationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(extractTour(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("findByDestination error", e);
        }
        return list;
    }

    public List<Tour> findToursByManagerCreated(int user_id) {
        String sql = "SELECT id, title, description, destination_id, start_date, end_date, base_price, creator_id FROM tours WHERE creator_id=?";
        List<Tour> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user_id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(extractTour(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("findByDestination error", e);
        }
        return list;
    }

    @Override
    public boolean create(Tour tour) {
        String sql = "INSERT INTO tours (title, description, destination_id, start_date, end_date, base_price, creator_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tour.getTitle());
            ps.setString(2, tour.getDescription());
            ps.setInt(3, tour.getDestinationId());
            ps.setDate(4, Date.valueOf(tour.getStartDate()));
            ps.setDate(5, Date.valueOf(tour.getEndDate()));
            ps.setDouble(6, tour.getBasePrice());
            ps.setInt(7, tour.getCreator_id());
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("create tour error", e);
        }
        return false;
    }

    @Override
    public boolean update(Tour tour) {
        String sql = "UPDATE tours SET title=?, description=?, destination_id=?, start_date=?, end_date=?, base_price=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, tour.getTitle());
            ps.setString(2, tour.getDescription());
            ps.setInt(3, tour.getDestinationId());
            ps.setDate(4, Date.valueOf(tour.getStartDate()));
            ps.setDate(5, Date.valueOf(tour.getEndDate()));
            ps.setDouble(6, tour.getBasePrice());
            ps.setInt(7, tour.getId());

            int rows = ps.executeUpdate();
            if (rows == 0) return false;

            // ← Вот тут получаем id из БД
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    tour.setId(generatedId); // сохраняем в объекте
                }
            }

            return true;

        } catch (Exception e) {
            logger.error("create tour error", e);
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM tours WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("delete tour error", e);
        }
        return false;
    }
    // === НОВЫЕ МЕТОДЫ ДЛЯ MANY-TO-MANY ===

    @Override
    public void clearRoomOptionsForTour(int tourId) {
        String sql = "DELETE FROM tour_room_options WHERE tour_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tourId);
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("clearRoomOptionsForTour error", e);
        }
    }

    @Override
    public void linkRoomOptionsToTour(int tourId, List<Integer> roomOptionIds) {
        if (roomOptionIds == null || roomOptionIds.isEmpty()) return;

        String sql = "INSERT INTO tour_room_options (tour_id, room_option_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int roomId : roomOptionIds) {
                ps.setInt(1, tourId);
                ps.setInt(2, roomId);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("linkRoomOptionsToTour error", e);
        }
    }
    @Override
    public int countSearchTours(String query, String startDate, String destinationId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM tours WHERE 1=1");

        if (query != null && !query.isEmpty()) {
            sql.append(" AND LOWER(title) LIKE LOWER(?)");
        }
        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND start_date >= ?");
        }
        if (destinationId != null && !destinationId.isEmpty()) {
            sql.append(" AND destination_id = ?");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (query != null && !query.isEmpty()) {
                ps.setString(paramIndex++, "%" + query + "%");
            }
            if (startDate != null && !startDate.isEmpty()) {
                ps.setDate(paramIndex++, Date.valueOf(startDate));
            }
            if (destinationId != null && !destinationId.isEmpty()) {
                ps.setInt(paramIndex++, Integer.parseInt(destinationId));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            logger.error("countSearchTours error", e);
        }

        return 0;
    }


    private Tour extractTour(ResultSet rs) throws SQLException {
        return new Tour(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getInt("destination_id"),
                null, // destination можно загрузить отдельно
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getDouble("base_price"),
                rs.getInt("creator_id"));
    }
}
