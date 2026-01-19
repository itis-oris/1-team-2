package com.tourlist.controller.Auth;

import com.tourlist.model.Role;
import com.tourlist.model.User;
import com.tourlist.service.RoleService;
import com.tourlist.service.UserService;
import com.tourlist.service.impl.RoleServiceImpl;
import com.tourlist.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserService userService;
    private RoleService roleService;

    @Override
    public void init() {
        userService = new UserServiceImpl();
        roleService = new RoleServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("templates/user/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        User user = userService.authenticate(email, password);

        if (user != null) {
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            String role = roleService.getRoleById(user.getRoleId()).getName();
            // Перенаправление в зависимости от роли
            switch (role) {
                case "MANAGER" -> resp.sendRedirect("manager/profile");
                default -> resp.sendRedirect("profile");
            }
        } else {
            req.setAttribute("error", "Неверный email или пароль");
            req.getRequestDispatcher("templates/user/login.jsp").forward(req, resp);
        }
    }
}

