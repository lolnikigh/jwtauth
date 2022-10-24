package me.aidimirov.jwtauth;

import lombok.RequiredArgsConstructor;
import me.aidimirov.jwtauth.model.Role;
import me.aidimirov.jwtauth.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DbInit implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) {
        userService.createRole(new Role(null, "ROLE_USER"));
    }
}
