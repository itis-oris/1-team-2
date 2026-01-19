package com.tourlist.service.impl;

import com.tourlist.dao.BookingDAO;
import com.tourlist.dao.TourDAO;
import com.tourlist.dao.UserDAO;
import com.tourlist.dao.impl.BookingDAOImpl;
import com.tourlist.dao.impl.TourDAOImpl;
import com.tourlist.dao.impl.UserDAOImpl;
import com.tourlist.model.Booking;
import com.tourlist.model.Tour;
import com.tourlist.model.User;
import com.tourlist.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

public class BookingServiceImpl implements BookingService {

    private final BookingDAO bookingDAO = new BookingDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();
    private final TourDAO tourDAO = new TourDAOImpl();

    @Override
    public Booking getBookingById(int id) {
        Booking booking = bookingDAO.findById(id);
        if (booking != null) {
            // Подгрузка связанных сущностей
            User user = userDAO.findById(booking.getUserId());
            Tour tour = tourDAO.findById(booking.getTourId());
            booking.setUser(user);
            booking.setTour(tour);
        }
        return booking;
    }

    @Override
    public List<Booking> getBookingsByUserId(int userId) {
        List<Booking> list = bookingDAO.findByUserId(userId);
        for (Booking b : list) {
            b.setUser(userDAO.findById(b.getUserId()));
            b.setTour(tourDAO.findById(b.getTourId()));
        }
        return list;
    }

    @Override
    public boolean createBooking(Booking booking) {
        // Заполняем недостающие данные перед сохранением
        if (booking.getBookingDate() == null) {
            booking.setBookingDate(LocalDateTime.now());
        }
        if (booking.getStatus() == null || booking.getStatus().isEmpty()) {
            booking.setStatus("PENDING");
        }

        // Например, можно пересчитать общую стоимость
        Tour tour = tourDAO.findById(booking.getTourId());
        if (tour != null && booking.getTotalPrice() <= 0) {
            double total = tour.getBasePrice() * booking.getPeopleCount();
            booking.setTotalPrice(total);
        }

        return bookingDAO.create(booking);
    }

    @Override
    public boolean updateBooking(Booking booking) {
        return bookingDAO.update(booking);
    }

    @Override
    public boolean cancelBooking(int id) {
        Booking booking = bookingDAO.findById(id);
        if (booking == null) return false;

        booking.setStatus("CANCELLED");
        return bookingDAO.update(booking);
    }

    @Override
    public boolean deleteBooking(int id) {
        return bookingDAO.delete(id);
    }
}

