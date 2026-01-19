<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css" />

<%@ include file="/templates/fragments/header.jsp" %>

<div class="container" style="max-width: 800px; margin: 30px auto;">
    <h2 style="text-align: center;">Редактировать тур</h2>

    <c:if test="${not empty errorMessage}">
        <div style="color: red; background: #ffe6e6; padding: 10px; margin-bottom: 20px; border-radius: 4px;">
            ${errorMessage}
        </div>
    </c:if>

    <form method="post" style="display: flex; flex-direction: column; gap: 15px;">
        <input type="hidden" name="id" value="${tour.id}">

        <div>
            <label><strong>Название тура:</strong></label>
            <input type="text" name="title" required style="width: 100%; padding: 8px;" value="${tour.title}">
        </div>

        <div>
            <label><strong>Описание:</strong></label>
            <textarea name="description" rows="4" style="width: 100%; padding: 8px;">${tour.description}</textarea>
        </div>

        <div style="position: relative;">
            <label><strong>Направление:</strong></label>
            <input type="text"
                   id="destinationInput"
                   value="${tour.destination.name}"
                   placeholder="Начните вводить город или страну..."
                   style="width: 100%; padding: 8px; margin-top: 5px;"
                   autocomplete="off">
            <input type="hidden" name="destinationId" id="destinationId" value="${tour.destinationId}" required>

            <div id="suggestions"
                 style="position: absolute; z-index: 1000; background: white; border: 1px solid #ccc;
                        width: 100%; max-height: 200px; overflow-y: auto; display: none;">
            </div>
        </div>

        <div style="display: flex; gap: 15px;">
            <div style="flex: 1;">
                <label><strong>Дата начала:</strong></label>
                <input type="date" name="startDate" required style="width: 100%; padding: 8px;" value="${tour.startDate}">
            </div>
            <div style="flex: 1;">
                <label><strong>Дата окончания:</strong></label>
                <input type="date" name="endDate" required style="width: 100%; padding: 8px;" value="${tour.endDate}">
            </div>
        </div>

        <div>
            <label><strong>Базовая цена (₽):</strong></label>
            <input type="number" step="0.01" name="basePrice" required min="0" style="width: 100%; padding: 8px;" value="${tour.basePrice}">
        </div>

        <!-- Варианты размещения -->
        <div>
            <label><strong>Варианты размещения:</strong></label>
            <div style="display: flex; flex-wrap: wrap; gap: 12px; margin-top: 8px;">
                <c:forEach var="room" items="${allRoomOptions}">
                    <c:set var="isChecked" value="false"/>
                    <c:forEach var="selectedId" items="${selectedRoomOptionIds}">
                        <c:if test="${room.id == selectedId}">
                            <c:set var="isChecked" value="true"/>
                        </c:if>
                    </c:forEach>
                    <label style="display: flex; align-items: center; gap: 6px; background: #f8f9fa; padding: 10px; border-radius: 6px; border: 1px solid #e9ecef;">
                        <input type="checkbox" name="roomOptions" value="${room.id}"
                               <c:if test="${isChecked}">checked</c:if>>
                        <span>
                            <strong>${room.roomType}</strong><br/>
                            <small>${room.capacity} гостей, ${room.bed_count} ${room.bed_type} кровать(и)</small>
                        </span>
                    </label>
                </c:forEach>
            </div>
            <small style="color: #6c757d;">Выберите один или несколько вариантов</small>
        </div>
        <!-- === Фотографии тура === -->
        <div>
            <label><strong>Фотографии:</strong></label>

            <!-- Отображение существующих фото -->
            <div style="display: flex; flex-wrap: wrap; gap: 10px; margin: 10px 0;">
                <!-- home.jpg (обложка) -->
                <div style="position: relative; width: 100px; height: 100px; border: 1px solid #ddd; border-radius: 4px; overflow: hidden;">
                    <img src="${pageContext.request.contextPath}/img/tour/${tour.id}/home.jpg"
                         onerror="this.parentElement.style.display='none'"
                         style="width: 100%; height: 100%; object-fit: cover;"
                         alt="Обложка">
                </div>

                <!-- photo2.jpg -->
                <div style="position: relative; width: 100px; height: 100px; border: 1px solid #ddd; border-radius: 4px; overflow: hidden;">
                    <img src="${pageContext.request.contextPath}/img/tour/${tour.id}/photo2.jpg"
                         onerror="this.parentElement.style.display='none'"
                         style="width: 100%; height: 100%; object-fit: cover;"
                         alt="Фото 2">
                </div>

                <!-- photo3.jpg -->
                <div style="position: relative; width: 100px; height: 100px; border: 1px solid #ddd; border-radius: 4px; overflow: hidden;">
                    <img src="${pageContext.request.contextPath}/img/tour/${tour.id}/photo3.jpg"
                         onerror="this.parentElement.style.display='none'"
                         style="width: 100%; height: 100%; object-fit: cover;"
                         alt="Фото 3">
                </div>

                <!-- photo4.jpg -->
                <div style="position: relative; width: 100px; height: 100px; border: 1px solid #ddd; border-radius: 4px; overflow: hidden;">
                    <img src="${pageContext.request.contextPath}/img/tour/${tour.id}/photo4.jpg"
                         onerror="this.parentElement.style.display='none'"
                         style="width: 100%; height: 100%; object-fit: cover;"
                         alt="Фото 4">
                </div>

                <!-- photo5.jpg -->
                <div style="position: relative; width: 100px; height: 100px; border: 1px solid #ddd; border-radius: 4px; overflow: hidden;">
                    <img src="${pageContext.request.contextPath}/img/tour/${tour.id}/photo5.jpg"
                         onerror="this.parentElement.style.display='none'"
                         style="width: 100%; height: 100%; object-fit: cover;"
                         alt="Фото 5">
                </div>
            </div>

            <!-- Загрузка новых фото -->
            <div style="margin-top: 15px;">
                <input type="file" name="newPhotos" accept="image/*" multiple
                       style="width: 100%; padding: 8px;" />
                <small style="color: #6c757d;">
                    Загрузите до 5 новых фото. Первое заменит обложку (<code>home.jpg</code>), остальные добавятся как <code>photo2.jpg</code>, <code>photo3.jpg</code> и т.д.<br>
                    Форматы: JPG, PNG.
                </small>
            </div>
        </div>

        <div style="text-align: center; margin-top: 20px; display: flex; justify-content: center; gap: 15px;">
            <button type="submit" style="padding: 10px 25px; background: #007bff; color: white; border: none; border-radius: 5px; cursor: pointer;">
                Сохранить изменения
            </button>

            <!-- Кнопка удаления -->
            <button type="button"
                    onclick="confirmDelete(${tour.id})"
                    style="padding: 10px 25px; background: #dc3545; color: white; border: none; border-radius: 5px; cursor: pointer;">
                Удалить тур
            </button>

            <a href="${pageContext.request.contextPath}/manager/profile"
               style="padding: 10px 15px; text-decoration: none; color: #6c757d; border: 1px solid #ccc; border-radius: 5px;">
                Отмена
            </a>
        </div>
    </form>
</div>

<script>
    const contextPath = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/js/destination.js"></script>

<script>
    function confirmDelete(tourId) {
        if (confirm('Вы уверены, что хотите удалить этот тур? Это действие нельзя отменить.')) {
            window.location.href = contextPath + '/manager/delete-tour?id=' + tourId;
        }
    }
</script>

<%@ include file="/templates/fragments/footer.jsp" %>