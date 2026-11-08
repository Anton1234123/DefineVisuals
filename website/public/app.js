"use strict";

const ONLINE_WINDOW_MS = 15 * 60 * 1000;
const USERS_PAGE_SIZE = 12;
const reducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)");

const state = {
  configs: [],
  configStatus: "loading",
  configQuery: "",
  configSort: "popular",
  users: [],
  userStatus: "loading",
  usersVisible: USERS_PAGE_SIZE,
};

let configRequestId = 0;
let userRequestId = 0;
let statsRequestId = 0;

const elements = {
  configGrid: document.getElementById("configs-grid"),
  configCount: document.getElementById("config-count"),
  configSearch: document.getElementById("config-search"),
  configSort: document.getElementById("config-sort"),
  configControls: document.getElementById("config-controls"),
  featured: document.getElementById("featured-config"),
  statsList: document.getElementById("stats-list"),
  statsMessage: document.getElementById("stats-message"),
  usersGrid: document.getElementById("users-grid"),
  usersMore: document.getElementById("users-more"),
};

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function icon(name) {
  return `<svg class="icon" aria-hidden="true"><use href="#icon-${name}"></use></svg>`;
}

function toCount(value) {
  const number = Number(value);
  return Number.isFinite(number) && number >= 0 ? number : null;
}

function formatNumber(value) {
  const number = toCount(value);
  return number === null ? "—" : new Intl.NumberFormat("ru-RU").format(number);
}

function formatDate(value) {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "дата не указана";
  return date.toLocaleDateString("ru-RU", { day: "numeric", month: "long", year: "numeric" });
}

function timeAgo(value) {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "давно";

  const diff = Math.max(0, Date.now() - date.getTime());
  const minutes = Math.floor(diff / 60000);
  if (minutes < 1) return "только что";
  if (minutes < 60) return `${minutes} мин назад`;

  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours} ч назад`;

  const days = Math.floor(hours / 24);
  if (days < 30) return `${days} дн назад`;
  return formatDate(value);
}

function plural(number, forms) {
  const value = Math.abs(number) % 100;
  const tail = value % 10;
  if (value > 10 && value < 20) return forms[2];
  if (tail > 1 && tail < 5) return forms[1];
  if (tail === 1) return forms[0];
  return forms[2];
}

async function fetchJson(url) {
  const response = await fetch(url, { headers: { Accept: "application/json" } });
  if (!response.ok) throw new Error(`HTTP ${response.status}`);

  const data = await response.json();
  if (!data || data.ok === false) throw new Error(data?.error || "invalid_response");
  return data;
}

function statePanel({ title, message, action, actionLabel, error = false, iconName = "file" }) {
  return `
    <div class="state-panel${error ? " state-panel--error" : ""}">
      ${icon(iconName)}
      <h3>${escapeHtml(title)}</h3>
      <p>${escapeHtml(message)}</p>
      ${action ? `<button class="button button--primary" type="button" data-action="${escapeHtml(action)}">${escapeHtml(actionLabel)}</button>` : ""}
    </div>`;
}

function configSkeletons() {
  return Array.from({ length: 3 }, () => `
    <div class="config-skeleton" aria-hidden="true">
      <span></span><span></span><span></span>
    </div>`).join("");
}

function userSkeletons() {
  return Array.from({ length: 4 }, () => `
    <div class="user-skeleton" aria-hidden="true">
      <span></span><span></span>
    </div>`).join("");
}

function renderFeaturedLoading() {
  elements.featured.setAttribute("aria-busy", "true");
  elements.featured.innerHTML = `
    <div class="featured-skeleton" aria-hidden="true">
      <span></span><span></span><span></span>
    </div>
    <span class="sr-only">Загружаем самый скачиваемый конфиг</span>`;
}

function configPopularity(left, right) {
  const downloadDifference = (toCount(right.downloads) ?? 0) - (toCount(left.downloads) ?? 0);
  if (downloadDifference !== 0) return downloadDifference;
  return new Date(right.createdAt).getTime() - new Date(left.createdAt).getTime();
}

function renderFeatured() {
  elements.featured.setAttribute("aria-busy", "false");

  if (state.configStatus === "error") {
    elements.featured.innerHTML = statePanel({
      title: "Cloud не ответил",
      message: "Проверь соединение и попробуй загрузить данные ещё раз.",
      action: "retry-configs",
      actionLabel: "Повторить",
      error: true,
      iconName: "refresh",
    });
    return;
  }

  if (!state.configs.length) {
    elements.featured.innerHTML = statePanel({
      title: "Пока без лидера",
      message: "Самый скачиваемый конфиг появится здесь после первой публикации.",
    });
    return;
  }

  const featured = [...state.configs].sort(configPopularity)[0];
  const name = escapeHtml(featured.name);
  const owner = escapeHtml(featured.owner);
  const downloadUrl = `/api/configs/${encodeURIComponent(String(featured.id))}/download?file=1`;

  elements.featured.innerHTML = `
    <article class="featured-config">
      <div class="featured-config__type">Конфиг .def</div>
      <h3>${name}</h3>
      <p class="featured-config__author">Автор: <strong>${owner}</strong></p>
      <div class="featured-config__meta">
        <span>${formatNumber(featured.downloads)} ${plural(toCount(featured.downloads) ?? 0, ["скачивание", "скачивания", "скачиваний"])}</span>
        <span>${formatDate(featured.createdAt)}</span>
      </div>
      <a class="button button--primary" href="${downloadUrl}" aria-label="Скачать конфиг ${name} в формате .def">
        ${icon("download")} Скачать .def
      </a>
    </article>`;
}

function captureConfigPositions() {
  const positions = new Map();
  elements.configGrid.querySelectorAll(".config-card[data-config-id]").forEach((card) => {
    positions.set(card.dataset.configId, card.getBoundingClientRect());
  });
  return positions;
}

function animateConfigReorder(previousPositions) {
  if (reducedMotion.matches || previousPositions.size === 0 || !("animate" in Element.prototype)) return;

  requestAnimationFrame(() => {
    elements.configGrid.querySelectorAll(".config-card[data-config-id]").forEach((card) => {
      const previous = previousPositions.get(card.dataset.configId);
      if (!previous) return;

      const current = card.getBoundingClientRect();
      const deltaX = previous.left - current.left;
      const deltaY = previous.top - current.top;
      if (Math.abs(deltaX) < 1 && Math.abs(deltaY) < 1) return;

      card.animate(
        [
          { transform: `translate(${deltaX}px, ${deltaY}px)`, boxShadow: "0 6px 18px rgba(96, 45, 70, 0.09)" },
          { transform: "translate(0, -6px)", boxShadow: "0 24px 58px rgba(96, 45, 70, 0.18)", offset: 0.76 },
          { transform: "translate(0, 0)", boxShadow: "0 6px 18px rgba(96, 45, 70, 0.09)" },
        ],
        { duration: 430, easing: "cubic-bezier(0.16, 1, 0.3, 1)" }
      );
    });
  });
}

function getVisibleConfigs() {
  const query = state.configQuery.trim().toLocaleLowerCase("ru-RU");
  const filtered = query
    ? state.configs.filter((config) => `${config.name ?? ""} ${config.owner ?? ""}`.toLocaleLowerCase("ru-RU").includes(query))
    : [...state.configs];

  if (state.configSort === "newest") {
    filtered.sort((left, right) => new Date(right.createdAt).getTime() - new Date(left.createdAt).getTime());
  } else if (state.configSort === "name") {
    filtered.sort((left, right) => String(left.name ?? "").localeCompare(String(right.name ?? ""), "ru", { sensitivity: "base" }));
  } else {
    filtered.sort(configPopularity);
  }

  return filtered;
}

function renderConfigCard(config) {
  const id = String(config.id);
  const name = escapeHtml(config.name);
  const owner = escapeHtml(config.owner);
  const downloads = toCount(config.downloads) ?? 0;
  const downloadUrl = `/api/configs/${encodeURIComponent(id)}/download?file=1`;

  return `
    <article class="config-card" data-config-id="${escapeHtml(id)}">
      <div class="config-card__top">
        <span class="config-marker" aria-hidden="true"></span>
        <span class="config-card__downloads">${icon("download")}${formatNumber(downloads)}</span>
      </div>
      <h3>${name}</h3>
      <p class="config-card__author">Автор: <strong>${owner}</strong></p>
      <p class="config-card__date">Опубликован ${formatDate(config.createdAt)}</p>
      <a class="button button--secondary" href="${downloadUrl}" aria-label="Скачать конфиг ${name} в формате .def">
        ${icon("download")} Скачать .def
      </a>
    </article>`;
}

function renderCatalog({ animate = false } = {}) {
  const previousPositions = animate ? captureConfigPositions() : new Map();
  elements.configGrid.setAttribute("aria-busy", state.configStatus === "loading" ? "true" : "false");

  if (state.configStatus === "loading") {
    elements.configCount.textContent = "Загрузка…";
    elements.configGrid.innerHTML = configSkeletons();
    return;
  }

  if (state.configStatus === "error") {
    elements.configCount.textContent = "Недоступно";
    elements.configGrid.innerHTML = statePanel({
      title: "Не удалось загрузить каталог",
      message: "Cloud может быть временно недоступен. Повтори запрос через несколько секунд.",
      action: "retry-configs",
      actionLabel: "Загрузить снова",
      error: true,
      iconName: "refresh",
    });
    return;
  }

  const configs = getVisibleConfigs();
  const total = state.configs.length;
  elements.configCount.textContent = state.configQuery
    ? `${formatNumber(configs.length)} из ${formatNumber(total)}`
    : `${formatNumber(total)} ${plural(total, ["конфиг", "конфига", "конфигов"])}`;

  if (total === 0) {
    elements.configGrid.innerHTML = statePanel({
      title: "Облако пока пустое",
      message: "Первый конфиг можно опубликовать из Configurations → Cloud внутри клиента.",
    });
    return;
  }

  if (configs.length === 0) {
    elements.configGrid.innerHTML = statePanel({
      title: "Ничего не найдено",
      message: "Попробуй другое название или сбрось поиск.",
      action: "clear-search",
      actionLabel: "Сбросить поиск",
      iconName: "search",
    });
    return;
  }

  elements.configGrid.innerHTML = configs.map(renderConfigCard).join("");
  if (animate) animateConfigReorder(previousPositions);
}

async function loadConfigs() {
  const requestId = ++configRequestId;
  state.configStatus = "loading";
  elements.configSearch.disabled = true;
  elements.configSort.disabled = true;
  renderFeaturedLoading();
  renderCatalog();

  try {
    const data = await fetchJson("/api/configs");
    if (requestId !== configRequestId) return;
    if (!Array.isArray(data.configs)) throw new Error("invalid_configs");

    state.configs = data.configs;
    state.configStatus = "ready";
  } catch (error) {
    if (requestId !== configRequestId) return;
    console.error("configs:", error);
    state.configs = [];
    state.configStatus = "error";
  } finally {
    if (requestId !== configRequestId) return;
    elements.configSearch.disabled = false;
    elements.configSort.disabled = false;
    renderFeatured();
    renderCatalog();
  }
}

function setStatValue(id, value) {
  const count = toCount(value);
  if (count === null) throw new Error(`invalid_${id}`);
  document.getElementById(id).textContent = formatNumber(count);
}

async function loadStats() {
  const requestId = ++statsRequestId;
  elements.statsList.setAttribute("aria-busy", "true");
  elements.statsMessage.hidden = true;

  try {
    const data = await fetchJson("/api/stats");
    if (requestId !== statsRequestId) return;
    setStatValue("stat-users", data.users);
    setStatValue("stat-launches", data.launches);
    setStatValue("stat-configs", data.configs);
    setStatValue("stat-downloads", data.downloads);
  } catch (error) {
    if (requestId !== statsRequestId) return;
    console.error("stats:", error);
    ["stat-users", "stat-launches", "stat-configs", "stat-downloads"].forEach((id) => {
      document.getElementById(id).textContent = "—";
    });
    elements.statsMessage.hidden = false;
  } finally {
    if (requestId === statsRequestId) elements.statsList.setAttribute("aria-busy", "false");
  }
}

function isOnline(lastSeen) {
  const timestamp = new Date(lastSeen).getTime();
  return Number.isFinite(timestamp) && Date.now() - timestamp < ONLINE_WINDOW_MS;
}

function renderUserCard(user) {
  const username = String(user.username ?? "Игрок");
  const name = escapeHtml(username);
  const initial = escapeHtml(Array.from(username.trim())[0]?.toLocaleUpperCase("ru-RU") || "D");
  const online = isOnline(user.lastSeen);
  const launches = toCount(user.launches) ?? 0;
  const status = online ? "онлайн" : `был(а) ${timeAgo(user.lastSeen)}`;

  return `
    <article class="user-card">
      <div class="user-avatar-wrap" aria-hidden="true">
        <img class="user-avatar" src="https://mc-heads.net/avatar/${encodeURIComponent(username)}/64" alt="" width="50" height="50" loading="lazy" />
        <span class="user-avatar-fallback" hidden>${initial}</span>
      </div>
      <div class="user-card__body">
        <h3 title="${name}">${name}</h3>
        <p class="user-card__status${online ? " is-online" : ""}"><span class="status-dot" aria-hidden="true"></span>${escapeHtml(status)}</p>
        <p class="user-card__meta">${formatNumber(launches)} ${plural(launches, ["запуск", "запуска", "запусков"])} · с ${formatDate(user.firstSeen)}</p>
      </div>
    </article>`;
}

function attachAvatarFallbacks() {
  elements.usersGrid.querySelectorAll(".user-avatar").forEach((image) => {
    image.addEventListener("error", () => {
      image.hidden = true;
      const fallback = image.nextElementSibling;
      if (fallback) fallback.hidden = false;
    }, { once: true });
  });
}

function renderUsers() {
  elements.usersGrid.setAttribute("aria-busy", state.userStatus === "loading" ? "true" : "false");
  elements.usersMore.hidden = true;

  if (state.userStatus === "loading") {
    elements.usersGrid.innerHTML = userSkeletons();
    return;
  }

  if (state.userStatus === "error") {
    elements.usersGrid.innerHTML = statePanel({
      title: "Участники не загрузились",
      message: "Попробуй ещё раз — остальные разделы сайта продолжат работать.",
      action: "retry-users",
      actionLabel: "Повторить",
      error: true,
      iconName: "refresh",
    });
    return;
  }

  if (state.users.length === 0) {
    elements.usersGrid.innerHTML = statePanel({
      title: "Здесь пока тихо",
      message: "Участники появятся после добровольного подключения Cloud в клиенте.",
    });
    return;
  }

  const visibleUsers = state.users.slice(0, state.usersVisible);
  elements.usersGrid.innerHTML = visibleUsers.map(renderUserCard).join("");
  elements.usersMore.hidden = state.usersVisible >= state.users.length;
  elements.usersMore.textContent = `Показать ещё (${formatNumber(state.users.length - visibleUsers.length)})`;
  attachAvatarFallbacks();
}

async function loadUsers() {
  const requestId = ++userRequestId;
  state.userStatus = "loading";
  state.usersVisible = USERS_PAGE_SIZE;
  renderUsers();

  try {
    const data = await fetchJson("/api/users");
    if (requestId !== userRequestId) return;
    if (!Array.isArray(data.users)) throw new Error("invalid_users");

    state.users = data.users;
    state.userStatus = "ready";
  } catch (error) {
    if (requestId !== userRequestId) return;
    console.error("users:", error);
    state.users = [];
    state.userStatus = "error";
  } finally {
    if (requestId === userRequestId) renderUsers();
  }
}

elements.configControls.addEventListener("submit", (event) => event.preventDefault());

elements.configSearch.addEventListener("input", (event) => {
  state.configQuery = event.currentTarget.value;
  renderCatalog({ animate: true });
});

elements.configSort.addEventListener("change", (event) => {
  state.configSort = event.currentTarget.value;
  renderCatalog({ animate: true });
});

elements.usersMore.addEventListener("click", () => {
  state.usersVisible += USERS_PAGE_SIZE;
  renderUsers();
});

document.addEventListener("click", (event) => {
  const action = event.target.closest("[data-action]")?.dataset.action;
  if (!action) return;

  if (action === "retry-configs") loadConfigs();
  if (action === "retry-users") loadUsers();
  if (action === "retry-stats") loadStats();
  if (action === "clear-search") {
    state.configQuery = "";
    elements.configSearch.value = "";
    elements.configSearch.focus();
    renderCatalog({ animate: true });
  }
});

Promise.allSettled([loadStats(), loadConfigs(), loadUsers()]);
