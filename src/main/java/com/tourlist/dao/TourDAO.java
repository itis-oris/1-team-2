package com.tourlist.dao;


import com.tourlist.model.Tour;

import java.sql.SQLException;
import java.util.List;

public interface TourDAO {
    public List<Tour> search(String query, String startDate, String destinationId, int limit, int offset);
    Tour findById(int id);
    List<Tour> findAll();
    List<Tour> findByDestination(int destinationId);
    public List<Tour> findToursByManagerCreated(int user_id);
    boolean create(Tour tour);
    boolean update(Tour tour) throws SQLException, ClassNotFoundException;
    boolean delete(int id);
    public void clearRoomOptionsForTour(int tourId);
    public void linkRoomOptionsToTour(int tourId, List<Integer> roomOptionIds);
    public int countSearchTours(String query, String startDate, String destinationId);

}

