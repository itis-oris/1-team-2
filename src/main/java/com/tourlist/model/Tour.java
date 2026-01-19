package com.tourlist.model;
import java.time.LocalDate;
import java.util.List;

public class Tour {

    private int id;
    private String title;
    private String description;
    private int destinationId;
    private Destination destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private double basePrice;
    private int creator_id;
    private User creator;


    private List<RoomOption> roomOptions;
    public Tour() {}

    public Tour(int id, String title,String description, int destinationId, Destination destination,
                LocalDate startDate, LocalDate endDate, double basePrice, int creator_id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.destinationId = destinationId;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.basePrice = basePrice;
        this.creator_id = creator_id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getDestinationId() {
        return destinationId;
    }

    public Destination getDestination() {
        return destination;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public List<RoomOption> getRoomOptions() {
        return roomOptions;
    }

    public void setRoomOptions(List<RoomOption> roomOptions) {
        this.roomOptions = roomOptions;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public int getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(int creator_id) {
        this.creator_id = creator_id;
    }

    @Override
    public String toString() {
        return "Tour{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", destinationId=" + destinationId +
                ", destination=" + destination +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", basePrice=" + basePrice +
                '}';
    }
}

