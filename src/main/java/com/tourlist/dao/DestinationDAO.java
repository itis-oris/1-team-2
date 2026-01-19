package com.tourlist.dao;


import com.tourlist.model.Destination;

import java.util.List;

public interface DestinationDAO {
    Destination findById(int id);
    List<Destination> findAll();
    boolean create(Destination destination);
    boolean update(Destination destination);
    boolean delete(int id);
    List<Destination> findByNameContaining(String namePart, int limit);
}