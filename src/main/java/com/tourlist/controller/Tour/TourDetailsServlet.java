package com.tourlist.controller.Tour;

import com.tourlist.model.Booking;
import com.tourlist.model.RoomOption;
import com.tourlist.model.Tour;
import com.tourlist.model.User;
import com.tourlist.service.BookingService;
import com.tourlist.service.RoomOptionService;
import com.tourlist.service.TourService;
import com.tourlist.service.impl.BookingServiceImpl;
import com.tourlist.service.impl.RoomOptionServiceImpl;
import com.tourlist.service.impl.TourServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/tours")
public class TourDetailsServlet extends HttpServlet {

    private TourService tourService;
    private RoomOptionService roomOptionService;
    private BookingService bookingService;

    @Override
    public void init() {
        tourService = new TourServiceImpl();
        roomOptionService = new RoomOptionServiceImpl();
        bookingService = new BookingServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tour ID is required");
            return;
        }
        String realPath = req.getServletContext().getRealPath("/img/tour/" + idParam);
        File photoDir = new File(realPath);
        try {
            int tourId = Integer.parseInt(idParam);
            Tour tour = tourService.getTourById(tourId);

            if (tour == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Tour not found");
                return;
            }
            List<String> photoFileNames = new ArrayList<>();

            if (photoDir.exists() && photoDir.isDirectory()) {
                File[] files = photoDir.listFiles((dir, name) -> {
                    String lower = name.toLowerCase();
                    return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                            lower.endsWith(".png") || lower.endsWith(".webp");
                });

                if (files != null) {
                    for (File file : files) {
                        photoFileNames.add(file.getName());
                    }
                }
            }

            req.setAttribute("photoFileNames", photoFileNames);

            List<RoomOption> roomOptions = roomOptionService.getRoomOptionsByTour(tourId);

            req.setAttribute("tour", tour);
            req.setAttribute("roomOptions", roomOptions);

            req.getRequestDispatcher("/templates/tour/tour-details.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid tour ID format");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        // Проверка авторизации (предполагается, что user.id хранится в сессии)
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=unauthorized");
            return;
        }

        // Получаем пользователя из сессии
        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        // Получаем параметры формы
        String tourIdParam = req.getParameter("tourId");
        String peopleCountParam = req.getParameter("peopleCount");
        String roomsCountParam = req.getParameter("roomsCount");

        if (tourIdParam == null || peopleCountParam == null || roomsCountParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required booking parameters");
            return;
        }

        try {
            int tourId = Integer.parseInt(tourIdParam);
            int peopleCount = Integer.parseInt(peopleCountParam);
            int roomsCount = Integer.parseInt(roomsCountParam);

            // Защита от некорректных значений
            if (peopleCount <= 0 || roomsCount <= 0) {
                req.setAttribute("errorMessage", "Количество людей и комнат должно быть больше 0.");
                handleGetAndShowError(req, resp, tourId);
                return;
            }

            // Создаём объект бронирования
            Booking booking = new Booking();
            booking.setUserId(userId);
            booking.setTourId(tourId);
            booking.setPeopleCount(peopleCount);
            booking.setRoomsCount(roomsCount);
            booking.setBookingDate(LocalDateTime.now());
            // totalPrice и status будут установлены в BookingServiceImpl

            // Сохраняем бронь
            if (bookingService.createBooking(booking)) {
                // Успешно → редирект на страницу подтверждения или личный кабинет
                resp.sendRedirect(req.getContextPath() + "/profile?booking=success");
            } else {
                req.setAttribute("errorMessage", "Не удалось создать бронирование. Попробуйте позже.");
                handleGetAndShowError(req, resp, tourId);
            }

        } catch (NumberFormatException e) {
            req.setAttribute("errorMessage", "Некорректные данные формы.");
            handleGetAndShowError(req, resp, Integer.parseInt(tourIdParam));
        }
    }

    // Вспомогательный метод: повторно загрузить тур и показать ошибку
    private void handleGetAndShowError(HttpServletRequest req, HttpServletResponse resp, int tourId) throws ServletException, IOException {
        Tour tour = tourService.getTourById(tourId);
        List<RoomOption> roomOptions = roomOptionService.getRoomOptionsByTour(tourId);

        req.setAttribute("tour", tour);
        req.setAttribute("roomOptions", roomOptions);
        req.getRequestDispatcher("/templates/tour/tour-details.jsp").forward(req, resp);
    }
}