# ShopAppSpring — веб-приложение на Spring Boot

Учебный проект: интернет-магазин с админкой, авторизацией и управлением товарами и пользователями.

## Описание

Веб-приложение с ролевой моделью (USER / ADMIN):

- **Гость** — просмотр товаров, поиск по названию и цене, регистрация
- **Пользователь (USER)** — просмотр товаров, личный профиль, смена пароля
- **Администратор (ADMIN)** — полный CRUD над пользователями и товарами

Проект написан на **Spring Boot 3.5.3** с использованием **Spring MVC**, **Spring Data JPA**, **Spring Security** и **Thymeleaf**.

## Технологии

| Технология | Назначение |
|-----------|-----------|
| Java 21 | Язык программирования |
| Spring Boot 3.5.3 | Фреймворк и автоконфигурация |
| Spring MVC | Обработка HTTP-запросов |
| Spring Data JPA + Hibernate | Работа с БД через ORM |
| Spring Security | Аутентификация и авторизация |
| Thymeleaf | Шаблонизатор HTML |
| H2 Database | Встроенная реляционная БД |
| Maven | Система сборки |
| JUnit 5 + Mockito | Юнит- и интеграционные тесты |

## Архитектура

Слоистая архитектура (layered). Запрос проходит по цепочке:

1. **Controller** — принимает HTTP-запрос, кладёт данные в `Model`, возвращает имя шаблона
2. **Service** — бизнес-логика, валидация, транзакции (`@Transactional`)
3. **Repository** — доступ к БД через Spring Data JPA (автогенерируемые запросы)
4. **Model** — JPA-сущности (`User`, `Product`, `Role`), маппятся на таблицы H2
5. **Thymeleaf** — рендерит HTML из шаблона и данных `Model`

```
Controller -> Service -> Repository -> H2 Database
     |            |             |
 Thymeleaf   Бизнес-логика   JPA-сущности
```

**Пакеты:**

- `model` — JPA-сущности (`User`, `Product`, `Role`)
- `repository` — Spring Data JPA репозитории
- `service` — бизнес-логика (`UserService`, `ProductService`, `CustomUserDetailsService`)
- `controller` — веб-контроллеры (`HomeController`, `AuthController`, `PublicController`, `AdminController`, `AdminProductController`, `ProfileController`)
- `config` — настройки Spring (`SecurityConfig`, `DataInitializer`)
- `dto` — объекты для форм (`UserForm`, `ProductForm`, `PasswordChangeForm`)
- `exception` — глобальная обработка ошибок (`GlobalExceptionHandler`)

## Запуск

### Требования

- JDK 21+
- Maven 3.9+

### Шаги

1. Клонировать репозиторий:

   ```bash
   git clone https://github.com/Myers66/ShopAppSpring.git
   cd ShopAppSpring
   ```

2. Собрать и запустить:

   ```bash
   mvn clean spring-boot:run
   ```

   Или запустить `ShopAppSpringApplication.java` в IDE.

3. Открыть в браузере: [http://localhost:8080](http://localhost:8080)

## Тестовые данные

При первом запуске автоматически создаётся администратор:

- **Логин:** `admin`
- **Пароль:** `admin`

Дополнительно можно зарегистрировать обычного пользователя через `/register`.

## Функционал

### Публичные страницы

- `/` — главная
- `/products` — список товаров с поиском по названию и диапазону цен
- `/login`, `/register` — вход и регистрация

### Для пользователей

- `/profile` — личный профиль (данные аккаунта) и смена пароля (с проверкой текущего)

### Для администратора

- `/admin/users` — CRUD пользователей
  - список, поиск по логину, создание, редактирование, удаление
- `/admin/products` — CRUD товаров
  - список, создание, редактирование, удаление

### Служебные

- `/h2-console` — веб-консоль H2 (для отладки)
- `/error` — единая страница ошибок

## Конфигурация

Файл `src/main/resources/application.properties`:

- **Порт:** `8080`
- **H2 БД:** файл `./db/shop.mv.db` (данные сохраняются между перезапусками)
- **Hibernate:** `ddl-auto=update` (таблицы создаются автоматически по `@Entity`)
- **H2-консоль:** включена
- **open-in-view:** отключён (правильная работа с ленивой загрузкой)

## Тесты

```bash
mvn test
```

Покрытие:

| Тестовый класс | Кол-во | Что проверяет |
|----------------|--------|---------------|
| `UserServiceTest` | 6 | Создание, дубликат логина, валидация пароля, обновление |
| `ProductServiceTest` | 7 | Создание, отрицательная цена, отрицательный остаток, обновление, поиск по id |
| `AdminControllerTest` | 9 | MockMvc: список, форма, создание, валидация, редактирование, удаление, доступ по ролям |
| `SecurityConfigTest` | 7 | Публичные страницы, доступ USER/ADMIN к `/admin/**`, статика, H2-консоль |

## Безопасность

- Пароли хэшируются через **BCrypt** (`PasswordEncoder`)
- **CSRF-защита** включена (кроме H2-консоли — для её работы)
- Ролевая модель: `ROLE_USER` / `ROLE_ADMIN`
- Сессии на основе **Cookie** (стандарт Spring Security)
- Защита от SQL-инъекций (PreparedStatement через JPA)
- **Смена пароля** в профиле требует ввода текущего пароля

## Реализованные концепции

- **IoC и DI** — Spring управляет бинами и внедряет зависимости через конструкторы
- **MVC** — Controller -> Service -> Repository -> Model -> Thymeleaf
- **JPA/Hibernate** — маппинг сущностей на таблицы через аннотации
- **Spring Data JPA** — репозитории с автогенерируемыми запросами по имени метода
- **Spring Security** — аутентификация, авторизация, CSRF, BCrypt
- **Thymeleaf** — шаблонизатор с интеграцией Spring Security (`sec:authorize`)
- **Валидация** — `@NotBlank`, `@Size`, `@Positive` + `BindingResult`
- **Обработка ошибок** — `@ControllerAdvice`, единая страница `error.html`
- **Юнит-тесты** — JUnit 5 + Mockito
- **Интеграционные тесты** — MockMvc

## Структура проекта

```
ShopAppSpring/
├── src/
│   ├── main/
│   │   ├── java/ru/ystu/project/shopappspring/
│   │   │   ├── ShopAppSpringApplication.java
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── controller/
│   │   │   ├── config/
│   │   │   ├── dto/
│   │   │   └── exception/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/css/style.css
│   │       └── templates/
│   │           ├── index.html
│   │           ├── products.html
│   │           ├── login.html
│   │           ├── register.html
│   │           ├── profile.html
│   │           ├── error.html
│   │           └── admin/
│   │               ├── users.html
│   │               ├── user-form.html
│   │               ├── products.html
│   │               └── product-form.html
│   └── test/java/ru/ystu/project/shopappspring/
│       ├── ShopAppSpringApplicationTests.java
│       ├── service/
│       │   ├── UserServiceTest.java
│       │   └── ProductServiceTest.java
│       ├── controller/
│       │   └── AdminControllerTest.java
│       └── config/
│           └── SecurityConfigTest.java
├── pom.xml
└── README.md
```

## Автор

Иван Евдокимов, ЯГТУ, 2026
