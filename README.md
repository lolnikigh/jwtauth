# Авторизация и аутентификация с помощью JSON Web Token

## Описание

JSON Web Token (JWT) — это открытый стандарт (RFC 7519) для создания токенов доступа, основанный на формате JSON.
Как правило, используется для передачи данных для аутентификации в клиент-серверных приложениях. Токены создаются сервером, 
подписываются секретным ключом и передаются клиенту, который в дальнейшем использует данный токен для подтверждения своей личности. [Подробнее](https://en.wikipedia.org/wiki/JSON_Web_Token)

---

### Создание и использование токенов

1. Клиентское приложение отправляет POST запрос с данными пользователя на URI /login.
   Пример запроса
   `curl -d "username=testuser&password=12345" -H "Content-Type: application/x-www-form-urlencoded" -X POST http://localhost:8080/login`
3. Сервер авторизации обрабатывает запрос, и, если не возникло исключений, генерирует Токен доступа и Токен обновления с помощью секретного ключа.
   ```java
       @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        User user = (User) authResult.getPrincipal();

        String access_token = jwtService.accessToken(user, request);
        String refresh_token = jwtService.refreshToken(user, request);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
   ```
4. Сервер авторизации помещает Токены в тело ответа (в данном случае, можно и в заголовке).
   ```json
   {"access_token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9sb2dpbiIsImV4cCI6MTY2NjY4OTAxMywiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl19.bOphqTW8Wz7LWOatSLL2bv-xkzs_R4ng--bKG2ZdumwMW9mKZqlk0qzB2sIlY9QwEutM3DM_DkEuOxUdWpUUZA",
   "refresh_token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9sb2dpbiIsImV4cCI6MTY2NjY4OTMxM30.EP973Zhg9OSfS7zVX0M6fSpyCxEda70c2HdH9G13pAoZHgPtGmRjPd1waeY3GFmCk4skUZc6MZ5mqWBlj3pEhQ"}
   ```
6. Клиентское приложение отправляет запрос с Токеном доступа в заголовке Authorization для получения данных с сервера ресурсов.
   Пример запроса
   `curl -d "username=testuser&password=12345" -H "Content-Type: application/x-www-form-urlencoded" -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9sb2dpbiIsImV4cCI6MTY2NjY4OTAxMywiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl19.bOphqTW8Wz7LWOatSLL2bv-xkzs_R4ng--bKG2ZdumwMW9mKZqlk0qzB2sIlY9QwEutM3DM_DkEuOxUdWpUUZA" -X GET http://localhost:8080/user`
8. Сервер ресурсов проверяет токен, если токен подлинный, то клиентское приложение получает данные, которые он запросил.
   ```json
   {"firstname":"TestUser","lastname":"TestUser","username":"testuser"}
   ```
Если срок действия Токена доступа истек, клиентское приложение отправляет POST запрос на URI /user/token/refresh и сервер авторизации возвращает 
Токен доступа и Токен обновления. Если срок действия обоих токенов истек, пользователю потебуется авторизоваться снова.
