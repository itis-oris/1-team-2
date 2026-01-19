package com.tourlist.model;

import java.time.LocalDateTime;

public class Booking {

    private int id;
    private int userId;
    private User user;
    private int tourId;
    private Tour tour;
    private LocalDateTime bookingDate;
    private int peopleCount;
    private int roomsCount;
    private double totalPrice;
    private String status;

    public Booking() {}

    public Booking(int id, int userId, User user, int tourId, Tour tour,
                   LocalDateTime bookingDate, int peopleCount,
                   int roomsCount, double totalPrice, String status) {
        this.id = id;
        this.userId = userId;
        this.user = user;
        this.tourId = tourId;
        this.tour = tour;
        this.bookingDate = bookingDate;
        this.peopleCount = peopleCount;
        this.roomsCount = roomsCount;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(int peopleCount) {
        this.peopleCount = peopleCount;
    }

    public int getRoomsCount() {
        return roomsCount;
    }

    public void setRoomsCount(int roomsCount) {
        this.roomsCount = roomsCount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", userId=" + userId +
                ", user=" + user +
                ", tourId=" + tourId +
                ", tour=" + tour +
                ", bookingDate=" + bookingDate +
                ", peopleCount=" + peopleCount +
                ", roomsCount=" + roomsCount +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                '}';
    }
}
