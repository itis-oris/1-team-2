package com.tourlist.controller.Destination;

import com.tourlist.model.Destination;
import com.tourlist.service.DestinationService;
import com.tourlist.service.impl.DestinationServiceImpl;
import com.tourlist.util.JsonResponseWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/destinations/search")
public class DestinationSearchServlet extends HttpServlet {

    private DestinationService destinationService = new DestinationServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("q");

        List<Destination> results = new ArrayList<>();
        if (query != null && !query.trim().isEmpty()) {
            // Ограничиваем 5 результатами
            results = destinationService.searchByName(query.trim(), 5);
        }

        // Используем твою утилиту
        JsonResponseWriter.writeJson(resp, results);
    }

}