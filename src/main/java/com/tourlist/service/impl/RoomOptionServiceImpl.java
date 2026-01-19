package com.tourlist.service.impl;

import com.tourlist.service.RoomOptionService;
import com.tourlist.dao.RoomOptionDAO;
import com.tourlist.dao.impl.RoomOptionDAOImpl;
import com.tourlist.model.RoomOption;

import java.util.List;

public class RoomOptionServiceImpl implements RoomOptionService {

    private final RoomOptionDAO roomOptionDAO = new RoomOptionDAOImpl();

    @Override
    public RoomOption getRoomOptionById(int id) {
        return roomOptionDAO.findById(id);
    }

    @Override
    public List<RoomOption> getAllRoomOptions() {
        return roomOptionDAO.findAll();
    }

    @Override
    public List<RoomOption> getRoomOptionsByTour(int tourId) {
        return roomOptionDAO.findAllRoomsByTourId(tourId);
    }

    @Override
    public boolean createRoomOption(RoomOption option) {
        if (option.getPriceMultiplier() <= 0) {
            option.setPriceMultiplier(1.0);
        }
        return roomOptionDAO.create(option);
    }

    @Override
    public boolean updateRoomOption(RoomOption option) {
        return roomOptionDAO.update(option);
    }

    @Override
    public boolean deleteRoomOption(int id) {
        return roomOptionDAO.delete(id);
    }
}