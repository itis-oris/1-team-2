package com.tourlist.controller.Tour;

import com.tourlist.model.Tour;
import com.tourlist.service.DestinationService;
import com.tourlist.service.TourService;
import com.tourlist.service.impl.DestinationServiceImpl;
import com.tourlist.service.impl.TourServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/tours/search")
public class TourSearchServlet extends HttpServlet {

    private TourService tourService;
    private DestinationService destinationService = new DestinationServiceImpl();

    @Override
    public void init() {
        tourService = new TourServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String ajax = req.getParameter("ajax");
        String query = req.getParameter("query");
        String startDate = req.getParameter("startDate");


        String destinationIdParam = req.getParameter("destinationId");
        Integer destinationId = null;
        if (destinationIdParam != null && !destinationIdParam.isEmpty()) {
            try {
                destinationId = Integer.parseInt(destinationIdParam);
            } catch (NumberFormatException ignored) {
                // просто игнорируем, если не число(unreal)
            }
        }

        int page = parseInt(req.getParameter("page"), 1);
        int pageSize = 10;

        List<Tour> tours = tourService.searchTours(
                query,
                startDate,
                destinationId != null ? destinationId.toString() : null,
                page,
                pageSize
        );

        int totalTours = tourService.countSearchTours(
                query,
                startDate,
                destinationId != null ? destinationId.toString() : null
        );
        int totalPages = (totalTours % pageSize == 0)
                ? totalTours / pageSize
                : totalTours / pageSize + 1;

        req.setAttribute("tours", tours);
        req.setAttribute("page", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("currentPage", page);
        req.setAttribute("destinations", destinationService.getAllDestinations());


        if ("1".equals(ajax)) {
            req.getRequestDispatcher("/templates/tour/tour-results.jsp").forward(req, resp);

        } else {
            req.getRequestDispatcher("/templates/tour/tour-search.jsp").forward(req, resp);
        }
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
