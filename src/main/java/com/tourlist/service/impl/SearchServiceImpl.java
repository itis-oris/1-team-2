package com.tourlist.service.impl;


import com.tourlist.dao.impl.TourDAOImpl;
import com.tourlist.model.Tour;
import com.tourlist.service.SearchService;

import java.util.List;
import java.util.stream.Collectors;

public class SearchServiceImpl implements SearchService {

    private TourDAOImpl tourDAO = new TourDAOImpl();

    public List<Tour> searchTours(String query) {
        if (query == null || query.isEmpty()) {
            return tourDAO.findAll();
        }
        String lower = query.toLowerCase();
        return tourDAO.findAll().stream()
                .filter(t -> t.getTitle().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}
