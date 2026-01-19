package com.tourlist.controller.Manager;

import com.tourlist.filters.FilterManager;
import com.tourlist.model.RoomOption;
import com.tourlist.model.Tour;
import com.tourlist.model.User;
import com.tourlist.service.DestinationService;
import com.tourlist.service.RoomOptionService;
import com.tourlist.service.TourService;
import com.tourlist.service.impl.DestinationServiceImpl;
import com.tourlist.service.impl.RoomOptionServiceImpl;
import com.tourlist.service.impl.TourServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/manager/edit-tour")
public class EditTourServlet extends HttpServlet {

    private TourService tourService = new TourServiceImpl();
    private DestinationService destinationService = new DestinationServiceImpl();
    private RoomOptionService roomOptionService = new RoomOptionServiceImpl();

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

        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID тура обязателен");
            return;
        }

        try {
            int tourId = Integer.parseInt(idParam);
            Tour tour = tourService.getTourById(tourId);

            if (tour == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Тур не найден");
                return;
            }

            // Проверка: только автор может редактировать
            if (tour.getCreator_id() != currentUser.getId()) {
                resp.sendRedirect(req.getContextPath() + "/manager/profile");
                return;
            }

            // Загружаем данные для формы
            req.setAttribute("tour", tour);
            req.setAttribute("destinations", destinationService.getAllDestinations());
            req.setAttribute("allRoomOptions", roomOptionService.getAllRoomOptions());
            req.setAttribute("selectedRoomOptionIds", getSelectedRoomOptionIds(tourId));

            req.getRequestDispatcher("/templates/manager/edit-tour.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверный формат ID");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
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
            Tour existingTour = tourService.getTourById(tourId);

            if (existingTour == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Тур не найден");
                return;
            }

            // Проверка прав
            if (existingTour.getCreator_id() != currentUser.getId()) {
                resp.sendRedirect(req.getContextPath() + "/manager/profile");
                return;
            }

            // Получаем данные из формы
            String title = req.getParameter("title");
            String description = req.getParameter("description");
            String destinationIdStr = req.getParameter("destinationId");
            String startDateStr = req.getParameter("startDate");
            String endDateStr = req.getParameter("endDate");
            String basePriceStr = req.getParameter("basePrice");

            if (title == null || title.trim().isEmpty() ||
                    destinationIdStr == null || startDateStr == null || endDateStr == null || basePriceStr == null) {
                req.setAttribute("errorMessage", "Все поля обязательны.");
                forwardWithError(req, resp, tourId);
                return;
            }

            int destinationId = Integer.parseInt(destinationIdStr);
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            double basePrice = Double.parseDouble(basePriceStr);

            if (startDate.isAfter(endDate)) {
                req.setAttribute("errorMessage", "Дата окончания не может быть раньше даты начала.");
                forwardWithError(req, resp, tourId);
                return;
            }

            // Обновляем тур
            existingTour.setTitle(title.trim());
            existingTour.setDescription(description != null ? description.trim() : "");
            existingTour.setDestinationId(destinationId);
            existingTour.setStartDate(startDate);
            existingTour.setEndDate(endDate);
            existingTour.setBasePrice(basePrice);

            if (tourService.updateTour(existingTour)) {
                // Обновляем связь с RoomOption
                String[] selectedRoomIds = req.getParameterValues("roomOptions");
                List<Integer> roomOptionIds = new ArrayList<>();
                if (selectedRoomIds != null) {
                    for (String id : selectedRoomIds) {
                        try {
                            roomOptionIds.add(Integer.parseInt(id));
                        } catch (NumberFormatException ignored) {}
                    }
                }
                tourService.assignRoomOptionsToTour(tourId, roomOptionIds);

                resp.sendRedirect(req.getContextPath() + "/manager/profile?updated=success");
            } else {
                req.setAttribute("errorMessage", "Не удалось обновить тур.");
                forwardWithError(req, resp, tourId);
            }

        } catch (Exception e) {
            req.setAttribute("errorMessage", "Ошибка при обновлении: " + e.getMessage());
            forwardWithError(req, resp, Integer.parseInt(idParam));
        }
    }

    // Метод для получения уже выбранных RoomOption
    private List<Integer> getSelectedRoomOptionIds(int tourId) {
        List<RoomOption> selected = roomOptionService.getRoomOptionsByTour(tourId);
        List<Integer> ids = new ArrayList<>();
        for (RoomOption r : selected) {
            ids.add(r.getId());
        }
        return ids;
    }

    private void forwardWithError(HttpServletRequest req, HttpServletResponse resp, int tourId) throws ServletException, IOException {
        Tour tour = tourService.getTourById(tourId);
        req.setAttribute("tour", tour);
        req.setAttribute("destinations", destinationService.getAllDestinations());
        req.setAttribute("allRoomOptions", roomOptionService.getAllRoomOptions());
        req.setAttribute("selectedRoomOptionIds", getSelectedRoomOptionIds(tourId));
        req.getRequestDispatcher("/templates/manager/edit-tour.jsp").forward(req, resp);
    }
}
