<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css" />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="../fragments/header.jsp" %>

<div class="container" style="max-width: 900px; margin: 30px auto;">
    <c:if test="${not empty errorMessage}">
        <div style="color: red; background: #ffe6e6; padding: 12px; margin-bottom: 20px; border-radius: 5px;">
            ${errorMessage}
        </div>
    </c:if>

    <h1>${tour.title}</h1>

    <p><strong>Направление:</strong> ${tour.destination.name}</p>
    <p><strong>Даты:</strong> ${tour.startDate} – ${tour.endDate}</p>
    <p><strong>Цена от:</strong> ${tour.basePrice} ₽</p>

    <h2>Описание</h2>
    <p style="white-space: pre-wrap;">${tour.description}</p>

    <!-- Фотографии тура -->
    <div style="margin: 20px 0; display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-start;">
        <c:forEach var="fileName" items="${photoFileNames}">
            <img src="${pageContext.request.contextPath}/img/tour/${tour.id}/${fileName}"
                 alt="${fn:escapeXml(fileName)}"
                 onerror="this.style.display='none'"
                 style="max-width: 300px; max-height: 300px; object-fit: cover; border: 1px solid #eee; border-radius: 4px;">
        </c:forEach>
    </div>

    <!-- Варианты размещения -->
    <h2>Варианты размещения</h2>
    <c:choose>
        <c:when test="${empty roomOptions}">
            <p>Информация о номерах временно недоступна.</p>
        </c:when>
        <c:otherwise>
            <div style="display: flex; flex-direction: column; gap: 15px;">
                <c:forEach var="room" items="${roomOptions}">
                    <div style="padding: 12px; border: 1px solid #ddd; border-radius: 6px; background: #f8f9fa;">
                        <strong>${room.roomType}</strong><br/>
                        До ${room.capacity} гостей,
                        ${room.bed_count} ${room.bed_type} кровать(и),
                        Коэффициент цены: ${room.priceMultiplier}
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

    <!-- Кнопка редактирования (только для автора) -->
    <c:if test="${not empty sessionScope.user && sessionScope.user.id == tour.creator_id}">
        <div style="margin: 25px 0; text-align: center;">
            <a href="${pageContext.request.contextPath}/manager/edit-tour?id=${tour.id}"
               style="padding: 10px 20px; background-color: #ffc107; color: #212529; text-decoration: none; border-radius: 5px; font-weight: bold;">
                Редактировать тур
            </a>
        </div>
    </c:if>

    <!-- Форма бронирования -->
    <h2>Забронировать тур</h2>
    <form method="post" style="background: #f1f3f5; padding: 20px; border-radius: 8px;">
        <input type="hidden" name="tourId" value="${tour.id}" />

        <div style="margin-bottom: 15px;">
            <label><strong>Количество людей:</strong></label><br/>
            <input type="number" name="peopleCount" min="1" value="1" required
                   style="padding: 8px; width: 100%; max-width: 200px;" />
        </div>

        <div style="margin-bottom: 15px;">
            <label><strong>Количество комнат:</strong></label><br/>
            <input type="number" name="roomsCount" min="1" value="1" required
                   style="padding: 8px; width: 100%; max-width: 200px;" />
        </div>

        <button type="submit"
                style="padding: 10px 25px; background-color: #28a745; color: white; border: none; border-radius: 5px; font-size: 16px; cursor: pointer;">
            Забронировать
        </button>
    </form>

    <div style="margin-top: 30px; text-align: center;">
        <a href="${pageContext.request.contextPath}/tours" style="color: #6c757d; text-decoration: none;">← Все туры</a>
    </div>
</div>

<%@ include file="../fragments/footer.jsp" %>