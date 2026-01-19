<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css" />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../fragments/header.jsp" %>

<div class="container" style="max-width: 900px; margin: 30px auto;">
    <h1>Мой профиль</h1>

    <p><strong>Имя:</strong> ${manager.name}</p>
    <p><strong>Email:</strong> ${manager.email}</p>
    <p><strong>Роль:</strong> ${manager.role.name}</p>

    <div style="text-align: right; margin: 20px 0;">
        <a href="${pageContext.request.contextPath}/manager/create-tour"
           style="padding: 8px 16px; background-color: #28a745; color: white; text-decoration: none; border-radius: 4px; font-weight: bold;">
            + Создать тур
        </a>
    </div>

    <h2>Мои туры</h2>

    <c:choose>
        <c:when test="${empty tours}">
            <p>Вы пока не создали ни одного тура.</p>
        </c:when>
        <c:otherwise>
            <div style="display: flex; flex-direction: column; gap: 15px;">
                <c:forEach var="tour" items="${tours}">
                    <div style="border: 1px solid #ddd; padding: 15px; border-radius: 6px; background: #f9f9f9;">
                        <h3>${tour.title}</h3>
                        <p><strong>Направление:</strong> ${tour.destination.name}</p>
                        <p><strong>Даты:</strong> ${tour.startDate} – ${tour.endDate}</p>
                        <p><strong>Цена от:</strong> ${tour.basePrice} ₽</p>
                        <div>
                            <a href="${pageContext.request.contextPath}/tour?id=${tour.id}"
                               style="margin-right: 12px; color: #007bff; text-decoration: none;">Просмотр</a>
                            <a href="${pageContext.request.contextPath}/manager/edit-tour?id=${tour.id}"
                               style="color: #ffc107; text-decoration: none;">Редактировать</a>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

    <div style="margin-top: 30px; text-align: center;">
        <a href="${pageContext.request.contextPath}/"
           style="color: #6c757d; text-decoration: none;">← Вернуться на главную</a>
    </div>
</div>

<%@ include file="../fragments/footer.jsp" %>