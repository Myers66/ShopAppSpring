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

Слоистая архитектура (layered):
Controller ? Service ? Repository ? H2 Database
? ? ?
Thymeleaf Бизнес-логика JPA-сущности

**Пакеты:**
- `model` — JPA-сущности (`User`, `Product`, `Role`)
- `repository` — Spring Data JPA репозитории
- `service` — бизнес-логика
- `controller` — веб-контроллеры
- `config` — настройки Spring (Security, DataInitializer)
- `dto` — объекты для форм (`UserForm`, `ProductForm`, `PasswordChangeForm`)
- `exception` — глобальная обработка ошибок

## Запуск

### Требования
- JDK 21+
- Maven 3.9+

### Шаги

1. Клонировать репозиторий:
   ```bash
   git clone https://github.com/Myers66/ShopAppSpring.git
   cd ShopAppSpring
Собрать и запустить:

bash
mvn clean spring-boot:run
Или запустить ShopAppSpringApplication.java в IDE.

Открыть в браузере: http://localhost:8080

Тестовые данные
При первом запуске автоматически создаётся администратор:

Логин: admin

Пароль: admin

Дополнительно можно зарегистрировать обычного пользователя через /register.

Функционал
Публичные страницы
/ — главная

/products — список товаров с поиском по названию и цене

/login, /register — вход и регистрация

Для пользователей
/profile — личный профиль и смена пароля

Для администратора
/admin/users — CRUD пользователей

/admin/products — CRUD товаров

Служебные
/h2-console — веб-консоль H2 (для отладки)

/error — страница ошибок

Конфигурация
Файл src/main/resources/application.properties:

Порт: 8080

H2 БД: файл ./db/shop.mv.db

Hibernate: ddl-auto=update (таблицы создаются автоматически)

H2-консоль: включена

open-in-view: отключён (правильная работа с ленивой загрузкой)

Тесты
bash
mvn test
Покрытие:

UserServiceTest — 6 тестов (создание, валидация, обновление)

ProductServiceTest — 7 тестов (создание, валидация цены и остатка)

AdminControllerTest — 9 тестов (MockMvc — веб-слой)

SecurityConfigTest — 7 тестов (правила доступа)

Безопасность
Пароли хэшируются через BCrypt (PasswordEncoder)

CSRF-защита включена (кроме H2-консоли)

Ролевая модель: ROLE_USER / ROLE_ADMIN

Сессии на основе Cookie (стандарт Spring Security)

Защита от SQL-инъекций (PreparedStatement через JPA)

Реализованные концепции
IoC и DI — Spring управляет бинами и внедряет зависимости через конструкторы

MVC — Controller ? Service ? Repository ? Model ? Thymeleaf

JPA/Hibernate — маппинг сущностей на таблицы через аннотации

Spring Data JPA — репозитории с автогенерируемыми запросами по имени метода

Spring Security — аутентификация, авторизация, CSRF, BCrypt

Thymeleaf — шаблонизатор с интеграцией Spring Security (sec:authorize)

Валидация — @NotBlank, @Size, @Positive + BindingResult

Юнит-тесты — JUnit 5 + Mockito

Интеграционные тесты — MockMvc

Автор
Иван Евдокимов, ЯГТУ, 2026
