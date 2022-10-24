package me.aidimirov.jwtauth.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.aidimirov.jwtauth.config.JwtService;
import me.aidimirov.jwtauth.model.User;
import me.aidimirov.jwtauth.model.response.CurrentUserResponse;
import me.aidimirov.jwtauth.model.response.ProfileResponse;
import me.aidimirov.jwtauth.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> authenticated() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> optionalUser = userService.getUser(username);
        log.debug("Get authenticated user: {}", username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            log.debug("User: {}", user);
            return new ResponseEntity<>(new CurrentUserResponse(user), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> userByUsername(@PathVariable String username) {
        Optional<User> optionalUser = userService.getUser(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            log.debug("Getting user: {}", user);
            return new ResponseEntity<>(new ProfileResponse(user), HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring(7);
                String username = jwtService.getSubject(refresh_token);
                Optional<User> optionalUser = userService.getUser(username);

                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    String access_token = jwtService.accessToken(user, request);
                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("access_token", access_token);
                    tokens.put("refresh_token", refresh_token);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
                }
            } catch (Exception e) {
                log.error("error logging in: {}", e.getMessage());
                response.setHeader("error", e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }

        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }
}

@Data
class RoleToUserForm {

    private String username;
    private String roleName;
}

