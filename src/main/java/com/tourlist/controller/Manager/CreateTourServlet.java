package com.tourlist.controller.Manager;

import com.tourlist.filters.FilterManager;
import com.tourlist.model.Destination;
import com.tourlist.model.RoomOption;
import com.tourlist.model.Tour;
import com.tourlist.model.User;
import com.tourlist.service.DestinationService;
import com.tourlist.service.RoomOptionService;
import com.tourlist.service.TourService;
import com.tourlist.service.UserService;
import com.tourlist.service.impl.DestinationServiceImpl;
import com.tourlist.service.impl.RoomOptionServiceImpl;
import com.tourlist.service.impl.TourServiceImpl;
import com.tourlist.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 5 * 1024 * 1024,    // 5 MB на файл
        maxRequestSize = 25 * 1024 * 1024 // 25 MB всего
)
@WebServlet("/manager/create-tour")
public class CreateTourServlet extends HttpServlet {

    private TourService tourService = new TourServiceImpl();
    private DestinationService destinationService = new DestinationServiceImpl();
    private UserService userService = new UserServiceImpl();
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

        // Загружаем список направлений для выпадающего списка
        List<Destination> destinations = destinationService.getAllDestinations();
        req.setAttribute("destinations", destinations);
        List<RoomOption> allRoomOptions = roomOptionService.getAllRoomOptions();
        req.setAttribute("allRoomOptions", allRoomOptions);

        req.getRequestDispatcher("/templates/manager/create-tour.jsp").forward(req, resp);
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
        int managerId = currentUser.getRoleId();

        try {
            String title = req.getParameter("title");
            String description = req.getParameter("description");
            String destinationIdStr = req.getParameter("destinationId");
            String startDateStr = req.getParameter("startDate");
            String endDateStr = req.getParameter("endDate");
            String basePriceStr = req.getParameter("basePrice");

            if (title == null || title.trim().isEmpty() ||
                    destinationIdStr == null || startDateStr == null || endDateStr == null || basePriceStr == null) {
                req.setAttribute("errorMessage", "Все поля обязательны.");
                forwardWithError(req, resp);
                return;
            }

            int destinationId = Integer.parseInt(destinationIdStr);
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            double basePrice = Double.parseDouble(basePriceStr);

            if (startDate.isAfter(endDate)) {
                req.setAttribute("errorMessage", "Дата окончания не может быть раньше даты начала.");
                forwardWithError(req, resp);
                return;
            }

            Tour tour = new Tour();
            tour.setTitle(title.trim());
            tour.setDescription(description != null ? description.trim() : "");
            tour.setDestinationId(destinationId);
            tour.setStartDate(startDate);
            tour.setEndDate(endDate);
            tour.setBasePrice(basePrice);
            tour.setCreator_id(managerId);

            // Сначала создаём тур (без фото)
            if (tourService.createTour(tour)) {
                int tourId = tour.getId();

                // Папка для фото: webapp/img/tour/{id}
                String uploadDir = req.getServletContext().getRealPath("/img/tour/" + tourId);
                File uploadFolder = new File(uploadDir);
                if (!uploadFolder.exists()) {
                    uploadFolder.mkdirs();
                }

                // Получаем все части с именем "photos"
                Collection<Part> photoParts = req.getParts().stream()
                        .filter(part -> "photos".equals(part.getName()))
                        .toList();

                int count = 0;
                for (Part photoPart : photoParts) {
                    if (photoPart.getSize() == 0) continue;
                    if (count >= 5) break; // Максимум 5

                    String originalFileName = Paths.get(photoPart.getSubmittedFileName()).getFileName().toString();
                    String targetFileName;

                    if (count == 0) {
                        // Первое фото → home.jpg
                        targetFileName = "home.jpg";
                    } else {
                        targetFileName = sanitizeFileName(originalFileName);
                        // Ограничиваем длину и заменяем пробелы
                        if (targetFileName.length() > 50) {
                            String ext = "";
                            int dot = targetFileName.lastIndexOf('.');
                            if (dot > 0) {
                                ext = targetFileName.substring(dot);
                                targetFileName = targetFileName.substring(0, dot);
                            }
                            targetFileName = targetFileName.substring(0, Math.min(40, targetFileName.length())) + ext;
                        }
                    }

                    Path targetPath = Paths.get(uploadDir, targetFileName);
                    try {
                        Files.copy(photoPart.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        // Логируем, но не прерываем — тур уже создан
                        e.printStackTrace();
                    }

                    count++;
                }

                resp.sendRedirect(req.getContextPath() + "/manager/profile?created=success");
            } else {
                req.setAttribute("errorMessage", "Не удалось создать тур. Попробуйте позже.");
                forwardWithError(req, resp);
            }

        } catch (Exception e) {
            req.setAttribute("errorMessage", "Ошибка в данных: " + e.getMessage());
            forwardWithError(req, resp);
        }
    }

    private void forwardWithError(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("destinations", destinationService.getAllDestinations());
        req.getRequestDispatcher("/templates/manager/create-tour.jsp").forward(req, resp);
    }
    private String sanitizeFileName(String name) {
        if (name == null || name.isEmpty()) {
            return "image.jpg";
        }
        // Оставляем только безопасные символы
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}