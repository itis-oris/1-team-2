package com.tourlist.controller.Admin;

import com.tourlist.model.User;
import com.tourlist.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


import java.io.IOException;
import java.util.List;

@WebServlet("/admin/users")
public class AdminUserServlet extends HttpServlet {
    private UserServiceImpl userService;

    @Override
    public void init() {
        userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User current = (User) session.getAttribute("user");

        if (current == null || !"ADMIN".equals(current.getRole().getName())) {
            resp.sendRedirect(req.getContextPath() + "/access-denied.jsp");
            return;
        }

        List<User> users = userService.getAllUsers();
        req.setAttribute("users", users);
        req.getRequestDispatcher("/src/main/webapp/WEB-INF/templates/admin/admin-users.jsp").forward(req, resp);
    }
}

