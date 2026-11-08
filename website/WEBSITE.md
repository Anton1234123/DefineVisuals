# Define Visuals — сайт и облако

Бэкенд + мини-сайт комьюнити: статистика запусков клиента, список юзеров и облако конфигов.
Разворачивается на **Vercel Functions**, данные хранятся только в **PostgreSQL** (Neon) через `DATABASE_URL`.

## Структура под Vercel

```text
website/
├── server.js          # Express app: module.exports = app, без app.listen()
├── db.js              # PostgreSQL-only хранилище
├── dev-server.js      # локальный HTTP-сервер (на Vercel не используется)
├── vercel.json        # framework: express, регион, rewrites, headers
├── .vercelignore      # в деплой не едут src/, build/, run/ и Gradle-файлы
├── package.json
├── package-lock.json
├── public/            # статика, отдаётся CDN Vercel
│   ├── index.html
│   ├── privacy.html
│   ├── style.css
│   └── app.js
└── test-api.js        # smoke-тест HTTP-контракта
```

`server.js` лежит в папке `website/` и экспортирует Express-приложение:

```js
const app = createApp();
module.exports = app;
```

`app.listen(...)` намеренно отсутствует: на Vercel портом управляет платформа. Локально используется
`dev-server.js`, который добавляет `express.static` только для удобной разработки. На Vercel
`express.static()` игнорируется, а файлы из `public/**` отдаются CDN.

## Локальный запуск

Из папки `website/`:

```bash
cd website
npm install
```

Нужен PostgreSQL и переменная окружения `DATABASE_URL`:

```bash
# PowerShell
$env:DATABASE_URL="postgresql://user:password@host:5432/dbname?sslmode=require"
npm run dev
```

Сайт будет на <http://localhost:3000>. Без `DATABASE_URL` API отвечает `503 database_unavailable`:
in-memory fallback и запись в файлы удалены, потому что на serverless они теряются после cold start.

### Тесты

```bash
npm test
```

Smoke-тест поднимает приложение на случайном порту с тестовым in-memory store и проверяет HTTP-контракт:
ping, каталог, загрузку/замену, `isMine`, PUT, скачивание, счётчики, удаление, health, обработку ошибок.
Production-код при этом всегда работает только с PostgreSQL.

## Деплой на Vercel

1. Импортируй репозиторий `DefineVisuals` в Vercel.
2. **Root Directory** укажи `website` — там лежат `server.js`, `package.json` и `vercel.json`.
3. Framework Preset определится как **Express** (также зафиксировано в `vercel.json`).
4. Build Command и Output Directory оставь по умолчанию. Vercel сам выполнит `npm install`.
5. В **Settings → Environment Variables** добавь:
   - `DATABASE_URL` — pooled connection string из Neon.
   - Опционально `RATE_LIMIT_SALT` — своя соль для хеширования IP в rate limiter.
6. Deploy. Регион функций — `fra1` (Frankfurt), ближе к Neon.

Прод-домен проекта — `https://definevisual.vercel.app`. Его нужно прописать
в `CloudApi.DEFAULT_BASE_URL` на клиенте (или передать `-Ddv.api=https://...` для проверки).

### База данных

Схема создаётся автоматически при первом API-запросе:

- `users` — случайный Cloud ID, ник, версия, первый/последний запуск, число запусков;
- `configs` — публичные конфиги, владелец, содержимое, скачивания;
- `request_limits` — атомарный rate limit без хранения счётчиков в RAM инстанса.

Пул соединений (`pg.Pool`) переиспользуется в тёплом Function-инстансе, но сами данные не кешируются
в памяти: каждый запрос читает/пишет PostgreSQL. Для Neon используй **pooled** connection string,
чтобы не исчерпать лимит соединений при параллельных serverless-запросах.

## API

| Метод | Путь | Описание |
|---|---|---|
| GET | `/api/health` | Проверка приложения и БД |
| POST | `/api/ping` | Регистрация запуска: `{id, username, version}` |
| GET | `/api/stats` | Счётчики: юзеры/запуски/конфиги/скачивания |
| GET | `/api/users` | Публичный список юзеров (без ID!) |
| GET | `/api/configs` | Каталог конфигов (без содержимого); `?id=` добавляет `isMine` |
| POST | `/api/configs` | Залить конфиг: `{id, name, data}`; имя-дубликат заменяет старый |
| GET | `/api/configs/:id/download` | Скачать (`?file=1` — как `.def` файл) |
| PUT | `/api/configs/:id` | Обновить свой конфиг: `{id, name?, data?}` |
| DELETE | `/api/configs/:id` | Удалить свой конфиг `{id}` в теле |

ID юзера (`DV-XXXX-XXXX-XXXX-XXXX`) — единственный «ключ», поэтому публичные эндпоинты его не отдают.

## Переменные окружения

| Переменная | Обязательна | Назначение |
|---|---|---|
| `DATABASE_URL` | да | PostgreSQL connection string (Neon pooled) |
| `RATE_LIMIT_SALT` | нет | Соль для хеша IP в rate limiter |
| `PORT` | нет | Порт локального `dev-server.js` (по умолчанию 3000) |
