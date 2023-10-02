# Explore with Me
# Описание приложения и его основные сервисы:
Приложение представляет собой афишу, с помощью которой можно предложить какое-либо событие от выставки до похода
в кино и набрать компанию для участия в нём.

**Ссылка на git**
+ [feature_comments](https://github.com/IvanMikhailovOfficial/java-explore-with-me/tree/feature_comments)

**Основной сервис**

API основного сервиса разделите на три части:

публичная будет доступна без регистрации любому пользователю сети;

закрытая будет доступна только авторизованным пользователям;

административная — для администраторов сервиса.

**Сервис статистики**

Второй сервис — сервис статистики. Он будет собирать информацию:
- о количестве обращений пользователей к спискам событий.
- о количестве запросов к подробной информации о событии.

На основе этой информации должна формироваться статистика о работе приложения.

**Функционал сервиса статистики содержат:**

запись информации о том, что был обработан запрос к эндпоинту API;
предоставление статистики за выбранные даты по выбранному эндпоинту. 
Чтобы можно было использовать сервис статистики, разработан HTTP-клиент.
Он отправляет запросы и обрабатывает ответы.

# Использованные технологии:
+ [Java](https://www.java.com/)
+ [Spring Boot](https://spring.io/projects/spring-boot)
+ [Hibernate](https://hibernate.org)
+ [PostgreSQL](https://www.postgresql.org)
+ [Liquibase](https://www.liquibase.org)
+ [Docker Compose](https://www.docker.com)
+ [Apache Maven](https://maven.apache.org)
+ [Project Lombok](https://projectlombok.org)
+ [Postman](https://www.postman.com)
+ [IntelliJ IDEA](https://www.jetbrains.com/ru-ru/idea/)

### Спецификация REST API swagger

- [Основной сервис](https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json)
- [Сервис статистики](https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-stats-service-spec.json)

### Postman тесты:

- [Основной сервис](https://github.com/yandex-praktikum/java-explore-with-me/blob/main_svc/postman/ewm-main-service.json)
- [Сервис статистики](https://github.com/yandex-praktikum/java-explore-with-me/blob/stat_svc/postman/ewm-stat-service.json)

# Функционал приложения:
- **Проект реализован по микро-сервисной архитектуре:**

- main-service - реализация основной бизнес-логики

- stat - сбор и хранение статистики по обращению к публичным эндпоинтам/выполнение различных воборок по анализу работы приложения

# Инструкция по развертыванию проекта:
1. Скачать данный репозиторий

2. mvn clean

3. mvn package

4. docker-compose build

5. docker-compose up -d

# Схема базы данных приложения:
![img.png](img.png)