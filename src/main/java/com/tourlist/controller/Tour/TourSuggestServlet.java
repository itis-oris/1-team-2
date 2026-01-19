package com.tourlist.controller.Tour;

import com.tourlist.model.Tour;
import com.tourlist.service.TourService;
import com.tourlist.service.impl.TourServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/tours/suggest")
public class TourSuggestServlet extends HttpServlet {
    private final TourService tourService = new TourServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String query = req.getParameter("query");
        List<Tour> all = tourService.getAllTours();

        List<String> suggestions = all.stream()
                .map(Tour::getTitle)
                .filter(t -> t.toLowerCase().contains(query.toLowerCase()))
                .distinct()
                .limit(10)
                .toList();

        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(new org.json.JSONArray(suggestions).toString());
    }
}

