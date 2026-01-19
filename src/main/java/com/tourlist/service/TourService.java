package com.tourlist.service;

import com.tourlist.model.Tour;

import java.sql.SQLException;
import java.util.List;

public interface TourService {
    public List<Tour> searchTours(String query, String startDate, String destinationId, int page, int limit);
    public List<Tour> getToursByManagerCreatedId(int user_id);
    Tour getTourById(int id);
    List<Tour> getAllTours();
    List<Tour> getToursByDestination(int destinationId);
    boolean createTour(Tour tour);
    boolean updateTour(Tour tour) throws SQLException, ClassNotFoundException;
    boolean deleteTour(int id);
    public void assignRoomOptionsToTour(int tourId, List<Integer> roomOptionIds);
    public int countSearchTours(String query, String startDate, String destinationId);
}
