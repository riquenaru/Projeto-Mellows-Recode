'use strict';

const API_BASE_URL = window.MELLOWS_API_BASE_URL || `${window.location.origin}/api`;
const BOOKS_ENDPOINT = `${API_BASE_URL}/books`;

async function apiRequest(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        headers: { 'Content-Type': 'application/json', ...options.headers }
    });
    const payload = response.status === 204 ? null : await response.json().catch(() => null);
    if (!response.ok) {
        const error = new Error(payload?.message || payload?.detail || 'Não foi possível concluir a solicitação.');
        error.status = response.status;
        throw error;
    }
    return payload;
}

function createBookCard(book) {
    const article = document.createElement('article');
    article.className = 'book-card';

    const cover = document.createElement('img');
    cover.className = 'book-cover';
    cover.src = book.coverImage;
    cover.alt = `Capa de ${book.title}`;
    cover.loading = 'lazy';
    article.append(cover);

    const content = document.createElement('div');
    content.className = 'book-card-body';
    const title = document.createElement('h2');
    title.textContent = book.title;
    const author = document.createElement('p');
    author.textContent = book.author;
    const summary = document.createElement('p');
    summary.textContent = book.summary;
    const meta = document.createElement('div');
    meta.className = 'book-meta';
    const level = document.createElement('span');
    level.className = 'badge-soft';
    level.textContent = book.readingLevelLabel;
    const category = document.createElement('span');
    category.className = 'badge-soft';
    category.textContent = book.category;
    meta.append(level, category);
    const link = document.createElement('a');
    link.className = 'btn btn-outline-primary';
    link.href = `./livro.html?id=${encodeURIComponent(book.id)}`;
    link.textContent = 'Ver detalhes';
    content.append(title, author, summary, meta, link);
    article.append(content);
    return article;
}

async function loadCatalog() {
    const grid = document.querySelector('[data-book-grid]');
    if (!grid) return;
    const status = document.querySelector('[data-catalog-status]');
    const empty = document.querySelector('[data-empty-state]');
    const searchForm = document.querySelector('[data-catalog-filters]');
    const search = searchForm?.elements.q;
    const level = searchForm?.elements.level;

    async function refresh() {
        grid.replaceChildren();
        if (empty) empty.hidden = true;
        if (status) status.textContent = 'Carregando catálogo…';
        const params = new URLSearchParams();
        if (search?.value.trim()) params.set('q', search.value.trim());
        if (level?.value) params.set('level', level.value);
        try {
            const books = await apiRequest(`${BOOKS_ENDPOINT}?${params.toString()}`);
            books.forEach((book) => grid.append(createBookCard(book)));
            if (status) status.textContent = `${books.length} ${books.length === 1 ? 'livro encontrado' : 'livros encontrados'}.`;
            if (empty) empty.hidden = books.length > 0;
            document.querySelectorAll('[data-book-count]').forEach((node) => { node.textContent = books.length; });
        } catch {
            if (status) status.textContent = 'Não foi possível carregar o catálogo. Confira se a API está em execução.';
            if (empty) { empty.hidden = false; empty.querySelector('h3').textContent = 'Catálogo indisponível'; }
        }
    }

    searchForm?.addEventListener('submit', (event) => { event.preventDefault(); refresh(); });
    level?.addEventListener('change', refresh);
    document.querySelector('[data-reset-filters]')?.addEventListener('click', () => {
        if (search) search.value = '';
        if (level) level.value = '';
        refresh();
    });
    await refresh();
}

async function loadBookCount() {
    const counters = document.querySelectorAll('[data-book-count]');
    if (!counters.length) return;
    try {
        const books = await apiRequest(BOOKS_ENDPOINT);
        counters.forEach((counter) => { counter.textContent = books.length; });
    } catch {
        counters.forEach((counter) => { counter.textContent = 'algumas'; });
    }
}

function createGoogleBookCard(book) {
    const article = document.createElement('article');
    article.className = 'book-card';
    const cover = document.createElement('img');
    cover.className = 'book-cover';
    cover.src = book.thumbnail || './Img/melows.png';
    cover.alt = book.thumbnail ? `Capa de ${book.title}` : 'Capa indisponível';
    cover.loading = 'lazy';
    article.append(cover);

    const body = document.createElement('div');
    body.className = 'book-card-body';
    const title = document.createElement('h2'); title.textContent = book.title;
    const author = document.createElement('p'); author.textContent = book.authors?.length ? `Por ${book.authors.join(', ')}` : 'Autor não informado';
    const description = document.createElement('p'); description.textContent = book.description || 'Sem descrição disponível.';
    const meta = document.createElement('p'); meta.className = 'book-meta';
    if (book.publishedDate) { const date = document.createElement('span'); date.className = 'badge-soft'; date.textContent = `Publicado em ${book.publishedDate}`; meta.append(date); }
    const link = document.createElement('a'); link.className = 'btn btn-outline-primary';
    link.href = book.infoLink; link.target = '_blank'; link.rel = 'noopener noreferrer'; link.textContent = 'Ver no Google Books';
    body.append(title, author, description, meta, link);
    article.append(body);
    return article;
}

function searchGoogleBooks() {
    const form = document.querySelector('[data-google-books-search]');
    if (!form) return;
    const grid = document.querySelector('[data-google-books-grid]');
    const status = document.querySelector('[data-google-books-status]');
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (!form.reportValidity()) return;
        const query = form.elements.q.value.trim();
        grid.replaceChildren();
        status.textContent = 'Buscando livros…';
        const params = new URLSearchParams({ q: query });
        try {
            const books = await apiRequest(`${API_BASE_URL}/google-books?${params}`);
            books.forEach((book) => grid.append(createGoogleBookCard(book)));
            status.textContent = books.length ? `${books.length} resultados encontrados no Google Books.` : 'Nenhum livro encontrado. Tente outro título, autor ou assunto.';
        } catch (error) {
            status.textContent = error.status === 503
                ? 'A busca externa ainda não está configurada. Defina GOOGLE_BOOKS_API_KEY no servidor.'
                : error.message;
        }
    });
}

async function loadBookDetail() {
    const container = document.querySelector('[data-book-detail]');
    if (!container) return;
    const id = new URLSearchParams(window.location.search).get('id');
    if (!id || !/^\d+$/.test(id)) {
        container.innerHTML = '<div class="empty-state"><h1>Livro não encontrado</h1><p>Volte ao catálogo e escolha uma história.</p><a class="btn btn-primary" href="./leitura.html">Voltar ao catálogo</a></div>';
        return;
    }
    try {
        const book = await apiRequest(`${BOOKS_ENDPOINT}/${encodeURIComponent(id)}`);
        document.title = `${book.title} | Mellows`;
        const layout = document.createElement('div');
        layout.className = 'detail-layout';
        const cover = document.createElement('img');
        cover.className = 'detail-cover'; cover.src = book.coverImage; cover.alt = `Capa de ${book.title}`;
        const copy = document.createElement('div'); copy.className = 'detail-copy';
        const eyebrow = document.createElement('p'); eyebrow.className = 'eyebrow'; eyebrow.textContent = 'Detalhes da leitura';
        const title = document.createElement('h1'); title.textContent = book.title;
        const author = document.createElement('p'); author.textContent = `Por ${book.author}`;
        const summary = document.createElement('p'); summary.textContent = book.summary;
        const tags = document.createElement('p'); tags.className = 'book-meta';
        const level = document.createElement('span'); level.className = 'badge-soft'; level.textContent = book.readingLevelLabel;
        const category = document.createElement('span'); category.className = 'badge-soft'; category.textContent = book.category;
        tags.append(level, category);
        const back = document.createElement('a'); back.className = 'btn btn-outline-primary me-2'; back.href = './leitura.html'; back.textContent = 'Voltar às leituras';
        const request = document.createElement('a'); request.className = 'btn btn-primary'; request.href = `./trocaLivros.html?bookId=${encodeURIComponent(book.id)}#pedido`; request.textContent = 'Tenho interesse na troca';
        copy.append(eyebrow, title, author, summary, tags, back, request);
        layout.append(cover, copy);
        container.replaceChildren(layout);
    } catch {
        container.innerHTML = '<div class="empty-state"><h1>Livro não encontrado</h1><p>O catálogo pode estar indisponível. Volte e tente novamente.</p><a class="btn btn-primary" href="./leitura.html">Voltar ao catálogo</a></div>';
    }
}

async function submitSignup() {
    const form = document.querySelector('[data-signup-form]');
    if (!form) return;
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        const status = form.querySelector('[data-signup-status]');
        if (!form.reportValidity()) return;
        status.textContent = 'Enviando cadastro…';
        try {
            const result = await apiRequest(`${API_BASE_URL}/community-signups`, {
                method: 'POST', body: JSON.stringify({
                    name: form.elements.name.value.trim(),
                    email: form.elements.email.value.trim(),
                    consent: form.elements.consent.checked
                })
            });
            status.textContent = `Cadastro recebido. Obrigado por participar! (Protocolo ${result.id})`;
            form.reset();
        } catch (error) {
            status.textContent = error.status === 409 ? 'Este e-mail já está cadastrado.' : error.message;
        }
    });
}

async function submitExchangeRequest() {
    const form = document.querySelector('[data-exchange-form]');
    if (!form) return;
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (!form.reportValidity()) return;
        const status = form.querySelector('[data-exchange-status]');
        status.textContent = 'Enviando pedido…';
        const fields = new FormData(form);
        try {
            const result = await apiRequest(`${API_BASE_URL}/exchange-requests`, {
                method: 'POST', body: JSON.stringify({
                    bookId: Number(fields.get('bookId')),
                    name: String(fields.get('name')).trim(),
                    email: String(fields.get('email')).trim(),
                    message: String(fields.get('message') || '').trim()
                })
            });
            status.textContent = `Pedido registrado com sucesso. Protocolo ${result.id}.`;
            form.reset();
        } catch (error) {
            status.textContent = error.message;
        }
    });
}

async function loadExchangeBookOptions() {
    const select = document.querySelector('[data-exchange-book-options]');
    if (!select) return;
    try {
        const books = await apiRequest(BOOKS_ENDPOINT);
        books.forEach((book) => {
            const option = document.createElement('option'); option.value = book.id; option.textContent = book.title;
            select.append(option);
        });
        const requestedBook = new URLSearchParams(window.location.search).get('bookId');
        if (requestedBook && books.some((book) => String(book.id) === requestedBook)) select.value = requestedBook;
        if (!books.length) select.disabled = true;
    } catch {
        select.disabled = true;
        const option = document.createElement('option'); option.textContent = 'Catálogo indisponível'; option.value = '';
        select.append(option);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadCatalog();
    searchGoogleBooks();
    loadBookCount();
    loadBookDetail();
    submitSignup();
    loadExchangeBookOptions();
    submitExchangeRequest();
});
