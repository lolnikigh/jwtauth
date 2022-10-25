# jwtauth
JWT Authorization and authentication server.

## Описание

JSON Web Token (JWT) — это открытый стандарт (RFC 7519) для создания токенов доступа, основанный на формате JSON.
Как правило, используется для передачи данных для аутентификации в клиент-серверных приложениях. Токены создаются сервером, 
подписываются секретным ключом и передаются клиенту, который в дальнейшем использует данный токен для подтверждения своей личности. [Подробнее](https://en.wikipedia.org/wiki/JSON_Web_Token)

---

### Создание и использование токенов

1. Клиентское приложение отправляет POST запрос с данными пользователя на URI /login.
2. Сервер авторизации обрабатывает запрос, и, если не возникло исключений, генерирует Токен доступа и Токен обновления с помощью секретного ключа.
3. Сервер авторизации помещает Токены в тело ответа (в данном случае, можно и в заголовке).
4. Клиентское приложение отправляет запрос с Токеном доступа в заголовке Authorization для получения данных с сервера ресурсов.
5. Сервер ресурсов проверяет токен, если токен подлинный, то клиентское приложение получает данные, которые он запросил.

Если срок действия Токена доступа истек, клиентское приложение отправляет POST запрос на URI /user/token/refresh и сервер авторизации возвращает 
Токен доступа и Токен обновления. Если срок действия обоих токенов истек, пользователю потебуется авторизоваться снова.