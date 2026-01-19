package com.tourlist.service.impl;

import com.tourlist.dao.TourDAO;
import com.tourlist.dao.DestinationDAO;
import com.tourlist.dao.impl.RoomOptionDAOImpl;
import com.tourlist.dao.impl.TourDAOImpl;
import com.tourlist.dao.impl.DestinationDAOImpl;
import com.tourlist.model.RoomOption;
import com.tourlist.model.Tour;
import com.tourlist.model.Destination;
import com.tourlist.model.User;
import com.tourlist.service.TourService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TourServiceImpl implements TourService {

    private final TourDAOImpl tourDAO = new TourDAOImpl();
    private final DestinationDAO destinationDAO = new DestinationDAOImpl();
    private final RoomOptionDAOImpl roomOptionDAO = new RoomOptionDAOImpl();

    public List<Tour> searchTours(String query, String startDate, String destinationId, int page, int limit) {
        int offset = (page - 1) * limit;
        return tourDAO.search(query, startDate, destinationId, limit, offset);
    }

    public List<RoomOption> findAllRoomsByTourId(int tour_id) {
        List<RoomOption> list = roomOptionDAO.findAllRoomsByTourId(tour_id);
        return list;
    }

    @Override
    public Tour getTourById(int id) {
        Tour tour = tourDAO.findById(id);
        if (tour != null) {
            Destination dest = destinationDAO.findById(tour.getDestinationId());
            tour.setDestination(dest);
        }
        return tour;
    }

    @Override
    public List<Tour> getAllTours() {
        List<Tour> tours = tourDAO.findAll();
        for (Tour t : tours) {
            t.setDestination(destinationDAO.findById(t.getDestinationId()));
        }
        return tours;
    }

    @Override
    public List<Tour> getToursByDestination(int destinationId) {
        return tourDAO.findByDestination(destinationId);
    }

    public List<Tour> getToursByManagerCreatedId(int user_id) {
        return tourDAO.findToursByManagerCreated(user_id);
    }

    @Override
    public boolean createTour(Tour tour) {
        return tourDAO.create(tour);
    }

    @Override
    public boolean updateTour(Tour tour) throws SQLException, ClassNotFoundException {
        return tourDAO.update(tour);
    }

    @Override
    public boolean deleteTour(int id) {
        return tourDAO.delete(id);
    }

    @Override
    public void assignRoomOptionsToTour(int tourId, List<Integer> roomOptionIds) {
        if (roomOptionIds == null || roomOptionIds.isEmpty()) {
            tourDAO.clearRoomOptionsForTour(tourId); // удаляет все
            return;
        }
        tourDAO.clearRoomOptionsForTour(tourId);     // сначала очистить
        tourDAO.linkRoomOptionsToTour(tourId, roomOptionIds); // затем добавить новые
    }
    public int countSearchTours(String query, String startDate, String destinationId) {
        return tourDAO.countSearchTours(query,startDate, destinationId);
    }
}

