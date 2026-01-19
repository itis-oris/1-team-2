package com.tourlist.service;

import com.tourlist.model.Destination;
import java.util.List;

public interface DestinationService {
    Destination getDestinationById(int id);
    List<Destination> getAllDestinations();
    boolean addDestination(Destination destination);
    boolean updateDestination(Destination destination);
    boolean removeDestination(int id);
    List<Destination> searchByName(String namePart, int limit);
}
