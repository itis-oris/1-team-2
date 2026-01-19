
// Получаем контекстный путь (например, "/tourlist")

let debounceTimer;
const suggestionsContainer = document.getElementById('destinationSuggestions');
const destinationInput = document.getElementById('destinationInput');
const destinationIdInput = document.getElementById('destinationId');

destinationInput.addEventListener('input', function () {
    clearTimeout(debounceTimer);
    const query = this.value.trim();

    if (query.length < 2) {
        suggestionsContainer.style.display = 'none';
        return;
    }

    debounceTimer = setTimeout(() => {
        const url = contextPath + '/destinations/search?q=' + encodeURIComponent(query);
        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Сервер вернул ошибку: ' + response.status);
                }
                return response.json();
            })
            .then(destinations => {
                if (destinations.length === 0) {
                    suggestionsContainer.innerHTML = '<div style="padding: 8px; color: #888;">Ничего не найдено</div>';
                } else {
                    suggestionsContainer.innerHTML = destinations.map(d =>
                        `<div onclick="selectDestination(${d.id}, \`${d.name.replace(/`/g, '\\`')}\`)"`
                             + " style=\"padding: 8px 12px; cursor: pointer; border-bottom: 1px solid #eee;\">"
                             + d.name +
                        '</div>'
                    ).join('');
                }
                suggestionsContainer.style.display = 'block';
            })
            .catch(err => {
                console.error('AJAX ошибка:', err);
                suggestionsContainer.innerHTML = '<div style="padding: 8px; color: red;">Ошибка загрузки</div>';
                suggestionsContainer.style.display = 'block';
            });
    }, 300);
});

function selectDestination(id, name) {
    destinationInput.value = name;
    destinationIdInput.value = id;
    suggestionsContainer.style.display = 'none';
}

document.addEventListener('click', function(e) {
    if (!destinationInput.contains(e.target) && !suggestionsContainer.contains(e.target)) {
        suggestionsContainer.style.display = 'none';
    }
});