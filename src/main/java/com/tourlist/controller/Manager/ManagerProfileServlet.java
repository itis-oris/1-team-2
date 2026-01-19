package com.tourlist.controller.Manager;

import com.tourlist.filters.FilterManager;
import com.tourlist.model.Tour;
import com.tourlist.model.User;
import com.tourlist.service.TourService;
import com.tourlist.service.UserService;
import com.tourlist.service.impl.TourServiceImpl;
import com.tourlist.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/manager/profile")
public class ManagerProfileServlet extends HttpServlet {

    private TourService tourService = new TourServiceImpl();
    private UserService userService = new UserServiceImpl();

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
        int managerId = currentUser.getId();

        // Обновляем данные пользователя (на случай, если роль изменилась)
        currentUser = userService.getUserById(managerId);

        // Загружаем туры, созданные этим менеджером
        List<Tour> tours = tourService.getToursByManagerCreatedId(managerId);

        req.setAttribute("manager", currentUser);
        req.setAttribute("tours", tours);

        req.getRequestDispatcher("/templates/manager/profile.jsp").forward(req, resp);
    }
}