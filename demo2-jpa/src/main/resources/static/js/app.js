// ============================================================
// 前端逻辑：用浏览器原生 fetch 调用后端 REST API
// 后端由 Spring Boot 提供，前端文件放在 src/main/resources/static
// 所以 index.html 和后端共用同一个端口，无需额外配置。
// ============================================================

const API_BASE = '/api/books';
const form = document.querySelector('#book-form');
const bookList = document.querySelector('#book-list');
const countEl = document.querySelector('#count');
const formMsg = document.querySelector('#form-msg');

// 从后端拉取全部图书并渲染
async function loadBooks() {
    try {
        const res = await fetch(API_BASE);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const books = await res.json();
        renderBooks(books);
    } catch (err) {
        bookList.innerHTML = `<li class="empty error">加载失败：${err.message}</li>`;
    }
}

function renderBooks(books) {
    countEl.textContent = books.length ? `(${books.length} 本)` : '';
    if (!books.length) {
        bookList.innerHTML = '<li class="empty">暂无图书，先去左侧新增一本吧</li>';
        return;
    }
    bookList.innerHTML = books.map(b => `
        <li class="book-item">
            <span class="book-title">${escapeHtml(b.title)}</span>
            <span class="book-author">${escapeHtml(b.author)}</span>
            <span class="book-price">¥ ${b.price}</span>
        </li>
    `).join('');
}

// 提交新增图书
form.addEventListener('submit', async (e) => {
    e.preventDefault();
    formMsg.textContent = '';

    const payload = {
        title: document.querySelector('#title').value.trim(),
        author: document.querySelector('#author').value.trim(),
        price: Number(document.querySelector('#price').value)
    };

    try {
        const res = await fetch(API_BASE, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (res.status !== 201) throw new Error(`新增失败 HTTP ${res.status}`);
        const created = await res.json();
        formMsg.textContent = `✅ 已新增《${created.title}》 id=${created.id}`;
        form.reset();
        await loadBooks(); // 重新拉取列表
    } catch (err) {
        formMsg.textContent = `❌ ${err.message}`;
    }
});

// 防止 XSS：把返回文本中的特殊字符转义
function escapeHtml(str) {
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

// 页面加载即拉取一次
loadBooks();