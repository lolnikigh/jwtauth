package me.aidimirov.jwtauth.service;

import me.aidimirov.jwtauth.exception.UserAlreadyExistsException;
import me.aidimirov.jwtauth.model.Role;
import me.aidimirov.jwtauth.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    User createUser(User user) throws UserAlreadyExistsException;

    Role createRole(Role role);

    void addRoleToUser(String username, String roleName);

    Optional<User> getUser(String username);

    Iterable<User> getUsers();


    Iterable<User> saveAll(List<User> users);
}
