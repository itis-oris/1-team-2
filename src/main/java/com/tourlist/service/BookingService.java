package com.tourlist.service;

import com.tourlist.model.Booking;
import java.util.List;

public interface BookingService {

    Booking getBookingById(int id);
    List<Booking> getBookingsByUserId(int userId);
    boolean createBooking(Booking booking);
    boolean updateBooking(Booking booking);
    boolean cancelBooking(int id); // логическое удаление или изменение статуса
    boolean deleteBooking(int id); // физическое удаление
}
