package com.tourlist.filters;

import com.tourlist.model.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class FilterManager {
    public static boolean isNotManager(User user) {
        switch (user.getRoleId()) {
            case 2 -> {
                return false;
            }
            default -> {
                return true;
            }
        }
    }
}
