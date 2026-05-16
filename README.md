[README.md](https://github.com/user-attachments/files/27855720/README.md)
## 📄 **README.md для проекта Turning Point (TP)**

```markdown
# 🎮 Turning Point (TP) — Система управления киберспортивными турнирами

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-blue.svg)](https://kotlinlang.org/)
[![Ktor](https://img.shields.io/badge/Ktor-2.3.7-green.svg)](https://ktor.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-42.7.1-blue.svg)](https://www.postgresql.org/)
[![Exposed](https://img.shields.io/badge/Exposed-0.48.0-orange.svg)](https://github.com/JetBrains/Exposed)

## 📖 О проекте

**Turning Point (TP)** — это REST API для управления киберспортивными турнирами, командами, игроками и трансферами. Система позволяет:

- Управлять командами и игроками в дисциплинах Dota 2 и CS2
- Создавать и проводить турниры с призовыми фондами
- Регистрировать команды на турниры с проверкой свободных мест
- Осуществлять трансферы игроков между командами
- Фиксировать результаты матчей и распределять призовые

### 🎯 Сложные бизнес-операции

В проекте реализованы **4 сложные бизнес-операции** с использованием транзакций:

| № | Операция | Аналог из методички |
|---|----------|---------------------|
| 1 | Трансфер игрока | Перевод денег между счетами |
| 2 | Завершение матча | Оформление заказа с резервированием |
| 3 | Регистрация команды на турнир | Регистрация на мероприятие |
| 4 | Распределение призовых | Возврат товара с компенсацией |

## 🛠 Технологический стек

| Компонент | Технология | Версия |
|-----------|------------|--------|
| Язык программирования | Kotlin | 1.9.22 |
| Web-фреймворк | Ktor | 2.3.7 |
| ORM | Exposed | 0.48.0 |
| База данных | PostgreSQL | 42.7.1 |
| JSON сериализация | Gson | 2.10.1 |
| Документация API | Swagger/OpenAPI | 2.3.7 |
| Пулы соединений | HikariCP | 5.1.0 |
| Логирование | Logback | 1.4.14 |

## 🏗 Архитектура проекта

Проект следует **слоистой архитектуре**:

```
┌─────────────────────────────────────────────────────────┐
│                      Routes (API)                        │
│                   HTTP эндпоинты + Swagger               │
├─────────────────────────────────────────────────────────┤
│                      Service (Бизнес-логика)             │
│            Сложные операции, валидация, транзакции       │
├─────────────────────────────────────────────────────────┤
│                    Repository (Доступ к данным)          │
│                  Интерфейсы + Exposed реализация         │
├─────────────────────────────────────────────────────────┤
│                      Model (Сущности)                    │
│                    Таблицы БД + Data classes             │
├─────────────────────────────────────────────────────────┤
│                   PostgreSQL (База данных)               │
└─────────────────────────────────────────────────────────┘
```

### Структура проекта

```
src/main/kotlin/
├── Main.kt                    # Точка входа, запуск сервера
├── Routes.kt                  # HTTP эндпоинты + Swagger
├── LocalDateAdapter.kt        # Адаптер для дат в JSON
├── config/
│   └── DatabaseConfig.kt      # Подключение к БД, DI
├── console/                   # Консольное управление
│   ├── ConsoleCommands.kt     # Главный цикл консоли
│   ├── TeamCommands.kt        # Команды для команд
│   ├── PlayerCommands.kt      # Команды для игроков
│   ├── TournamentCommands.kt  # Команды для турниров
│   ├── MatchCommands.kt       # Команды для матчей
│   ├── TransferCommands.kt    # Команды для трансферов
│   └── StatsCommands.kt       # Статистика
├── model/                     # Модели данных (8 таблиц)
├── repository/                # Репозитории (интерфейсы + impl)
├── service/                   # Сервисы (бизнес-логика)
├── dto/                       # DTO для API
└── resources/
    └── openapi/
        └── documentation.yaml # Swagger спецификация
```

## 🗄 Структура базы данных

### Схема БД (8 таблиц)

```
┌─────────┐     ┌─────────────┐     ┌──────────────┐
│  games  │────<│    teams    │>────│   players    │
└─────────┘     └─────────────┘     └──────────────┘
                      │                     │
                      │                     │
                      ▼                     ▼
              ┌─────────────┐     ┌──────────────┐
              │ tournaments │     │  transfers   │
              └─────────────┘     └──────────────┘
                      │
                      │
                      ▼
              ┌─────────────────────┐
              │ tournament_registrations │
              └─────────────────────────┘
                      │
                      │
                      ▼
              ┌─────────────┐
              │   matches   │
              └─────────────┘
                      │
                      ▼
              ┌─────────────┐
              │  prize_logs │
              └─────────────┘
```

### Таблицы

| Таблица | Описание | Связи |
|---------|----------|-------|
| `games` | Дисциплины (Dota 2, CS2) | 1:N с teams, tournaments, players |
| `teams` | Киберспортивные команды | N:1 с games, 1:N с players |
| `players` | Игроки | N:1 с games, N:1 с teams |
| `tournaments` | Турниры | N:1 с games |
| `tournament_registrations` | Регистрация команд | N:M teams ↔ tournaments |
| `matches` | Матчи турниров | N:1 с tournaments |
| `transfers` | Трансферы игроков | N:1 с players, teams |
| `prize_logs` | Логи распределения призовых | N:1 с tournaments |

## 🚀 Запуск проекта

### Требования

- **JDK 17** или выше
- **PostgreSQL 14** или выше
- **IntelliJ IDEA** (рекомендуется)

### Инструкция по запуску

#### 1. Клонирование репозитория

```bash
git clone https://github.com/your-username/turning-point.git
cd turning-point
```

#### 2. Создание базы данных

Откройте **pgAdmin** или выполните в терминале:

```sql
CREATE DATABASE turning_point;
```

Затем выполните SQL скрипт из файла `database.sql` (создание таблиц и тестовых данных).

#### 3. Настройка подключения

Откройте `src/main/kotlin/config/DatabaseConfig.kt` и измените пароль:

```kotlin
password = "ваш_пароль"
```

#### 4. Запуск приложения

В IntelliJ IDEA:
- Откройте `Main.kt`
- Нажмите на зеленый треугольник рядом с `fun main()`

Или через терминал:

```bash
./gradlew run
```

#### 5. Проверка работы

После запуска откройте в браузере:

- **Swagger UI:** http://localhost:8080/swagger
- **OpenAPI JSON:** http://localhost:8080/openapi

## 📚 API Эндпоинты

### Основные сущности

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/games` | Все дисциплины |
| GET | `/teams` | Все команды (фильтр: `?gameId=1`) |
| GET | `/players` | Все игроки (фильтры: `?teamId=1`, `?gameId=1`) |
| GET | `/tournaments` | Все турниры (фильтр: `?gameId=1`) |
| GET | `/matches` | Все матчи (фильтр: `?tournamentId=1`) |
| GET | `/transfers` | Все трансферы |

### CRUD операции

| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/teams` | Создать команду |
| PUT | `/teams/{id}/budget` | Обновить бюджет |
| DELETE | `/teams/{id}` | Удалить команду |
| POST | `/players` | Создать игрока |
| PUT | `/players/{id}/team` | Перевести игрока |
| PUT | `/players/{id}/rating` | Изменить рейтинг |
| DELETE | `/players/{id}` | Удалить игрока |
| POST | `/tournaments` | Создать турнир |
| PUT | `/tournaments/{id}/status` | Изменить статус |
| DELETE | `/tournaments/{id}` | Удалить турнир |
| POST | `/matches` | Создать матч |
| DELETE | `/matches/{id}` | Удалить матч |

### Сложные операции

| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/transfers` | **Трансфер игрока** (проверка бюджета, списание/зачисление) |
| PUT | `/matches/{id}/finish` | **Завершение матча** (фиксация победителя) |
| POST | `/tournaments/{id}/register/{teamId}` | **Регистрация команды** (проверка мест) |
| POST | `/tournaments/{id}/distribute-prizes` | **Распределение призовых** (обновление бюджетов) |

## 💻 Консольное управление

После запуска сервера доступно консольное управление:

```
> help

📋 Доступные команды:

  teams list                    - показать все команды
  teams get <id>               - показать команду по ID
  teams add                    - добавить команду (интерактивно)
  teams budget <id> <сумма>    - изменить бюджет команды
  teams delete <id>            - удалить команду

  players list                  - показать всех игроков
  players get <id>              - показать игрока по ID
  players add                   - добавить игрока (интерактивно)
  players team <id> <teamId>    - перевести игрока в команду
  players rating <id> <рейтинг> - изменить рейтинг
  players delete <id>           - удалить игрока

  tournaments list              - показать все турниры
  tournaments get <id>          - показать турнир по ID
  tournaments add               - добавить турнир (интерактивно)
  tournaments status <id> <статус> - изменить статус
  tournaments register <tournamentId> <teamId> - зарегистрировать команду
  tournaments prizes <tournamentId> <teamId> <place> <prize> - распределить призовые
  tournaments delete <id>       - удалить турнир

  matches list                  - показать все матчи
  matches get <id>              - показать матч по ID
  matches add                   - добавить матч (интерактивно)
  matches finish <id> <winner> <score1> <score2> - завершить матч
  matches delete <id>           - удалить матч

  transfers list                - показать все трансферы
  transfers add <playerId> <toTeamId> <fee> - трансфер игрока
  transfers player <playerId>   - трансферы игрока

  stats                         - статистика по базе
  help                          - показать эту справку
  exit                          - остановить сервер
```

## 📊 Примеры запросов

### Трансфер игрока (сложная операция)

```bash
POST http://localhost:8080/transfers
Content-Type: application/json

{
    "playerId": 1,
    "toTeamId": 2,
    "fee": 50000
}
```

**Ответ:**
```json
{
    "message": "Yatoro перешёл из Team Spirit в Team Liquid за 50000.0!",
    "transfer": {
        "id": 1,
        "playerId": 1,
        "fromTeamId": 1,
        "toTeamId": 2,
        "fee": 50000.0,
        "status": "completed",
        "transferDate": "2026-05-16"
    }
}
```

### Завершение матча

```bash
PUT http://localhost:8080/matches/1/finish
Content-Type: application/json

{
    "winnerId": 1,
    "scoreTeam1": 2,
    "scoreTeam2": 1
}
```

### Регистрация команды на турнир

```bash
POST http://localhost:8080/tournaments/1/register/1
```

### Распределение призовых

```bash
POST http://localhost:8080/tournaments/1/distribute-prizes
Content-Type: application/json

[
    {"teamId": 1, "place": 1, "prizeAmount": 50000},
    {"teamId": 2, "place": 2, "prizeAmount": 30000},
    {"teamId": 3, "place": 3, "prizeAmount": 20000}
]
```

## 📋 Тестовые данные

В проекте предустановлены тестовые данные:

- **2 дисциплины:** Dota 2, CS2
- **16 команд** (8 Dota 2 + 8 CS2)
- **80 игроков** (40 Dota 2 + 40 CS2)
- **6 турниров** (с призовыми фондами)

## 🔧 Возможные проблемы и решения

### Ошибка подключения к БД

```kotlin
// Проверьте пароль в DatabaseConfig.kt
password = "ваш_пароль"
```

### Swagger UI не открывается

Убедитесь, что файл `src/main/resources/openapi/documentation.yaml` существует.

### Консольные команды не работают

Убедитесь, что сервер запущен и вы вводите команды в консоль после `> `.

## 📈 Планы по развитию

- [ ] Добавить аутентификацию и авторизацию
- [ ] Реализовать WebSocket для live-матчей
- [ ] Добавить рейтинг игроков (ELO система)
- [ ] Создать фронтенд на React/Vue
- [ ] Добавить экспорт статистики в CSV/PDF

## 👨‍💻 Автор

- **Turning Point Team** — *Курсовая работа* — [GitHub](https://github.com/)

## 📄 Лицензия

Этот проект является учебной работой и не предназначен для коммерческого использования.

---

**🎮 Turning Point — Твоя точка невозврата в мир киберспорта!**
```

---

## 📁 **Дополнительные файлы для GitHub**

### **.gitignore**

```gitignore
# Compiled files
*.class
*.jar
*.war

# Gradle
.gradle/
build/
!gradle/wrapper/gradle-wrapper.jar

# IDEA
.idea/
*.iml
*.iws
*.ipr
out/

# Logs
*.log

# OS files
.DS_Store
Thumbs.db

# Local configuration
local.properties

# Database
*.db
*.sqlite
```

### **database.sql**

```sql
-- Создание базы данных
CREATE DATABASE turning_point;

-- Подключитесь к базе и выполните:

-- 1. Дисциплины
CREATE TABLE games (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- 2. Команды
CREATE TABLE teams (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    game_id INT REFERENCES games(id),
    budget DECIMAL(15,2) DEFAULT 100000,
    captain_id INT,
    created_at DATE DEFAULT CURRENT_DATE
);

-- 3. Игроки
CREATE TABLE players (
    id SERIAL PRIMARY KEY,
    nickname VARCHAR(50) NOT NULL,
    real_name VARCHAR(100),
    game_id INT REFERENCES games(id),
    team_id INT REFERENCES teams(id) ON DELETE SET NULL,
    rating INT DEFAULT 1000,
    role VARCHAR(50)
);

-- 4. Турниры
CREATE TABLE tournaments (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    game_id INT REFERENCES games(id),
    prize_pool DECIMAL(15,2) DEFAULT 0,
    max_teams INT NOT NULL,
    start_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'registration'
);

-- 5. Регистрация команд на турнир
CREATE TABLE tournament_registrations (
    team_id INT REFERENCES teams(id) ON DELETE CASCADE,
    tournament_id INT REFERENCES tournaments(id) ON DELETE CASCADE,
    registered_at DATE DEFAULT CURRENT_DATE,
    place INT,
    PRIMARY KEY (team_id, tournament_id)
);

-- 6. Матчи
CREATE TABLE matches (
    id SERIAL PRIMARY KEY,
    tournament_id INT REFERENCES tournaments(id) ON DELETE CASCADE,
    round INT NOT NULL,
    team1_id INT REFERENCES teams(id),
    team2_id INT REFERENCES teams(id),
    winner_id INT REFERENCES teams(id),
    score_team1 INT DEFAULT 0,
    score_team2 INT DEFAULT 0,
    scheduled_date DATE,
    status VARCHAR(20) DEFAULT 'scheduled'
);

-- 7. Трансферы
CREATE TABLE transfers (
    id SERIAL PRIMARY KEY,
    player_id INT REFERENCES players(id) ON DELETE CASCADE,
    from_team_id INT REFERENCES teams(id),
    to_team_id INT REFERENCES teams(id),
    fee DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    transfer_date DATE DEFAULT CURRENT_DATE
);

-- 8. Логи призовых
CREATE TABLE prize_logs (
    id SERIAL PRIMARY KEY,
    tournament_id INT REFERENCES tournaments(id),
    distribution TEXT,
    distributed_at DATE DEFAULT CURRENT_DATE
);

-- Вставка тестовых данных
INSERT INTO games (name) VALUES ('Dota 2'), ('CS2');

-- Dota 2 команды
INSERT INTO teams (name, game_id, budget, created_at) VALUES
('Team Spirit', 1, 250000, '2024-01-01'),
('Team Liquid', 1, 300000, '2024-01-01'),
('Gaimin Gladiators', 1, 220000, '2024-01-01'),
('BetBoom Team', 1, 180000, '2024-01-01');

-- CS2 команды
INSERT INTO teams (name, game_id, budget, created_at) VALUES
('FaZe Clan', 2, 280000, '2024-01-01'),
('Team Vitality', 2, 260000, '2024-01-01'),
('G2 Esports', 2, 250000, '2024-01-01'),
('NAVI', 2, 300000, '2024-01-01');

-- Турниры
INSERT INTO tournaments (name, game_id, prize_pool, max_teams, start_date, status) VALUES
('The International 2026', 1, 3000000, 16, '2026-08-15', 'registration'),
('IEM Cologne 2026', 2, 1000000, 16, '2026-07-25', 'registration');
```
