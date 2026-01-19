package com.tourlist.dao;



import com.tourlist.dao.impl.*;
import com.tourlist.model.*;

import java.time.LocalDateTime;
import java.util.List;

public class TestDAO {
    public static void main(String[] args) {

        // ----------------- UserDAO -----------------
        UserDAOImpl userDAO = new UserDAOImpl();
        RoleDAOImpl roleDAO = new RoleDAOImpl();

        // Найти пользователя
        User user = userDAO.findById(1);
        System.out.println("Find user by ID: " + user);

        // Создать нового пользователя
        User newUser = new User();
        newUser.setName("Test User");
        newUser.setEmail("testuser@mail.com");
        newUser.setPasswordHash("hashedPassword");
        newUser.setRoleId(2); // Предположим, роль USER
        boolean created = userDAO.create(newUser);
        System.out.println("User created: " + created);

        // Найти всех пользователей
        List<User> allUsers = userDAO.findAll();
        allUsers.forEach(System.out::println);

        // ----------------- RoleDAO -----------------
        Role role = roleDAO.findByName("ADMIN");
        System.out.println("Role by name: " + role);

        // ----------------- DestinationDAO -----------------
        DestinationDAOImpl destinationDAO = new DestinationDAOImpl();
        List<Destination> destinations = destinationDAO.findAll();
        destinations.forEach(System.out::println);

        // ----------------- TourDAO -----------------
        TourDAOImpl tourDAO = new TourDAOImpl();
        List<Tour> tours = tourDAO.findAll();
        tours.forEach(System.out::println);

        // ----------------- RoomOptionDAO -----------------
        RoomOptionDAOImpl roomDAO = new RoomOptionDAOImpl();
        List<RoomOption> rooms = roomDAO.findAll();
        rooms.forEach(System.out::println);

        // ----------------- BookingDAO -----------------
        BookingDAOImpl bookingDAO = new BookingDAOImpl();

        // Создать бронирование
        Booking booking = new Booking();
        booking.setUserId(1);
        booking.setTourId(1);
        booking.setBookingDate(LocalDateTime.now());
        booking.setPeopleCount(2);
        booking.setRoomsCount(1);
        booking.setTotalPrice(1000.0);
        booking.setStatus("PENDING");

        boolean bookingCreated = bookingDAO.create(booking);
        System.out.println("Booking created: " + bookingCreated);

        // Получить бронирования по пользователю
        List<Booking> bookings = bookingDAO.findByUserId(1);
        bookings.forEach(System.out::println);
    }
}

