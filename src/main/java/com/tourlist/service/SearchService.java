package com.tourlist.service;

import com.tourlist.model.Tour;

import java.util.List;

public interface SearchService {
    public List<Tour> searchTours(String query);
}
