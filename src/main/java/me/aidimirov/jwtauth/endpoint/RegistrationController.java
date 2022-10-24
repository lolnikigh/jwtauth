package me.aidimirov.jwtauth.endpoint;

import lombok.RequiredArgsConstructor;
import me.aidimirov.jwtauth.exception.UserAlreadyExistsException;
import me.aidimirov.jwtauth.model.User;
import me.aidimirov.jwtauth.model.request.RegistrationUserRequest;
import me.aidimirov.jwtauth.model.response.SuccessfulRegisterResponse;
import me.aidimirov.jwtauth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody RegistrationUserRequest payload) {
        User user = toUser(payload);

        try {
            userService.createUser(user);
            URI uri = URI.create(ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/user/" + user.getUsername()).toUriString());

            return ResponseEntity.created(uri)
                    .body(new SuccessfulRegisterResponse(user));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    public User toUser(RegistrationUserRequest payload) {
        return new User(payload.getFirstname(), payload.getLastname(), payload.getUsername(), payload.getPassword(), payload.getEmail());
    }
}

