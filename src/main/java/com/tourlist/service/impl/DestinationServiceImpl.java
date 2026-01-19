package com.tourlist.service.impl;

import com.tourlist.dao.DestinationDAO;
import com.tourlist.dao.impl.DestinationDAOImpl;
import com.tourlist.model.Destination;
import com.tourlist.service.DestinationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class DestinationServiceImpl implements DestinationService {

    private static final Logger logger = LogManager.getLogger(DestinationServiceImpl.class);
    private final DestinationDAO destinationDAO = new DestinationDAOImpl();


    @Override
    public Destination getDestinationById(int id) {
        if (id <= 0) {
            logger.warn("Invalid destination id: {}", id);
            return null;
        }
        return destinationDAO.findById(id);
    }

    @Override
    public List<Destination> getAllDestinations() {
        return destinationDAO.findAll();
    }

    @Override
    public boolean addDestination(Destination destination) {
        if (destination == null || destination.getName() == null || destination.getName().isEmpty()) {
            logger.warn("Cannot add destination: invalid data");
            return false;
        }
        return destinationDAO.create(destination);
    }

    @Override
    public boolean updateDestination(Destination destination) {
        if (destination == null || destination.getId() <= 0) {
            logger.warn("Cannot update destination: invalid id");
            return false;
        }
        return destinationDAO.update(destination);
    }

    @Override
    public boolean removeDestination(int id) {
        if (id <= 0) {
            logger.warn("Cannot remove destination: invalid id");
            return false;
        }
        return destinationDAO.delete(id);
    }
    @Override
    public List<Destination> searchByName(String namePart, int limit) {
        return destinationDAO.findByNameContaining(namePart, limit);
    }
}
