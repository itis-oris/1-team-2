package com.tourlist.controller.Manager;

import com.tourlist.filters.FilterManager;
import com.tourlist.model.User;
import com.tourlist.service.TourService;
import com.tourlist.service.impl.TourServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/manager/delete-tour")
public class DeleteTourServlet extends HttpServlet {

    private TourService tourService = new TourServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (FilterManager.isNotManager(currentUser)){
            resp.sendRedirect(req.getContextPath() + "/index");
            return;
        }
        String idParam = req.getParameter("id");

        if (idParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID тура обязателен");
            return;
        }

        try {
            int tourId = Integer.parseInt(idParam);
            var tour = tourService.getTourById(tourId);

            if (tour == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Тур не найден");
                return;
            }

            // Только автор может удалить
            if (tour.getCreator_id() != currentUser.getId()) {
                resp.sendRedirect(req.getContextPath() + "/manager/profile");
                return;
            }

            if (tourService.deleteTour(tourId)) {
                resp.sendRedirect(req.getContextPath() + "/manager/profile?deleted=success");
            } else {
                resp.sendRedirect(req.getContextPath() + "/manager/profile?deleted=error");
            }

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверный ID");
        }
    }
}