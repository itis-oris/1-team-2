package com.tourlist.dao;


import com.tourlist.model.Booking;

import java.util.List;

public interface BookingDAO {
    Booking findById(int id);
    List<Booking> findByUserId(int userId);
    boolean create(Booking booking);
    boolean update(Booking booking);
    boolean delete(int id);
}

