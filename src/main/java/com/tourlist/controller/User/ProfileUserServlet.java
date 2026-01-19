package com.tourlist.controller.User;

import com.tourlist.model.User;
import com.tourlist.model.Booking;
import com.tourlist.service.UserService;
import com.tourlist.service.BookingService;
import com.tourlist.service.impl.UserServiceImpl;
import com.tourlist.service.impl.BookingServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/profile")
public class ProfileUserServlet extends HttpServlet {

    private UserService userService;
    private BookingService bookingService;

    @Override
    public void init() {
        userService = new UserServiceImpl();
        bookingService = new BookingServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() +  "/login");
            return;
        }

        // Подгружаем бронирования пользователя
        List<Booking> bookings = bookingService.getBookingsByUserId(user.getId());

        req.setAttribute("user", user);
        req.setAttribute("bookings", bookings);

        req.getRequestDispatcher("/templates/user/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() +  "/login");
            return;
        }

        // Изменяем имя пользователя
        String newName = req.getParameter("name");
        if (newName != null && !newName.trim().isEmpty()) {
            user.setName(newName.trim());
            userService.updateUser(user); // предполагаем, что UserService умеет обновлять пользователя
            session.setAttribute("user", user); // обновляем сессию
        }

        resp.sendRedirect(req.getContextPath() + "/profile");
    }
}
