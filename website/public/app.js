// Define Visuals mini-site: подгружаем статистику, юзеров и конфиги с нашего API.
"use strict";

const ONLINE_WINDOW_MS = 15 * 60 * 1000; // онлайн, если запуск был < 15 минут назад

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function formatNumber(n) {
  return new Intl.NumberFormat("ru-RU").format(n);
}

function formatDate(ts) {
  return new Date(ts).toLocaleDateString("ru-RU", { day: "numeric", month: "long", year: "numeric" });
}

function timeAgo(ts) {
  const diff = Date.now() - ts;
  const minutes = Math.floor(diff / 60000);
  if (minutes < 1) return "только что";
  if (minutes < 60) return `${minutes} мин назад`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours} ч назад`;
  const days = Math.floor(hours / 24);
  if (days < 30) return `${days} дн назад`;
  return formatDate(ts);
}

async function fetchJson(url) {
  const res = await fetch(url);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return res.json();
}

async function loadStats() {
  try {
    const data = await fetchJson("/api/stats");
    document.getElementById("stat-users").textContent = formatNumber(data.users);
    document.getElementById("stat-launches").textContent = formatNumber(data.launches);
    document.getElementById("stat-configs").textContent = formatNumber(data.configs);
    document.getElementById("stat-downloads").textContent = formatNumber(data.downloads);
  } catch (err) {
    console.error("stats:", err);
  }
}

async function loadUsers() {
  const grid = document.getElementById("users-grid");
  try {
    const data = await fetchJson("/api/users");
    if (!data.users.length) {
      grid.innerHTML = '<div class="empty">Пока никого нет — стань первым! ❤️</div>';
      return;
    }
    grid.innerHTML = data.users
      .map((u) => {
        const name = escapeHtml(u.username);
        const online = Date.now() - u.lastSeen < ONLINE_WINDOW_MS;
        const status = online
          ? '<span class="user-online">● онлайн</span>'
          : `был(а) ${timeAgo(u.lastSeen)}`;
        return `
          <div class="user-card">
            <img class="user-avatar" src="https://mc-heads.net/avatar/${encodeURIComponent(u.username)}/46"
                 alt="" loading="lazy" onerror="this.src='https://mc-heads.net/avatar/MHF_Steve/46'" />
            <div>
              <div class="user-name">${name}</div>
              <div class="user-meta">с нами с ${formatDate(u.firstSeen)}<br />${status} · запусков: ${formatNumber(u.launches)}</div>
            </div>
          </div>`;
      })
      .join("");
  } catch (err) {
    console.error("users:", err);
    grid.innerHTML = '<div class="empty">Не удалось загрузить юзеров 😢</div>';
  }
}

async function loadConfigs() {
  const list = document.getElementById("configs-list");
  try {
    const data = await fetchJson("/api/configs");
    if (!data.configs.length) {
      list.innerHTML = '<div class="empty">Облако пустое — залей первый конфиг из клиента!</div>';
      return;
    }
    list.innerHTML = data.configs
      .map(
        (c) => `
        <div class="config-card">
          <div class="config-icon">⚙️</div>
          <div class="config-info">
            <div class="config-name">${escapeHtml(c.name)}</div>
            <div class="config-meta">от ${escapeHtml(c.owner)} · ${formatDate(c.createdAt)} · скачиваний: ${formatNumber(c.downloads)}</div>
          </div>
          <a class="config-download" href="/api/configs/${c.id}/download?file=1">Скачать .def</a>
        </div>`
      )
      .join("");
  } catch (err) {
    console.error("configs:", err);
    list.innerHTML = '<div class="empty">Не удалось загрузить конфиги 😢</div>';
  }
}

loadStats();
loadUsers();
loadConfigs();
