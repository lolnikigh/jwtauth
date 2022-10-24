package me.aidimirov.jwtauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.aidimirov.jwtauth.data.RoleRepository;
import me.aidimirov.jwtauth.data.UserRepository;
import me.aidimirov.jwtauth.exception.UserAlreadyExistsException;
import me.aidimirov.jwtauth.model.Role;
import me.aidimirov.jwtauth.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        throw new UsernameNotFoundException(String.format("User '%s' not found in the database", username));
    }

    @Override
    public User createUser(User user) throws UserAlreadyExistsException {

        String username = user.getUsername();
        String email = user.getEmail();

        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException(String.format("The username %s is already in use", username));
        }
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(String.format("The email %s is already in use", email));
        }

        user.setPassword(encoder.encode(user.getPassword()));
        Role role_user = roleRepository.findByName("ROLE_USER");
        log.debug(role_user.toString());
        user.getRoles().add(role_user);
        log.debug("Saving new user to database: {}", user);
        return userRepository.save(user);
    }

    @Override
    public Role createRole(Role role) {
        log.debug("Saving new role to database: {}", role);
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Role role = roleRepository.findByName("ROLE_USER");
            user.getRoles().add(role);
            log.debug("Add role '{}' to user '{}'", "ROLE_USER", user.getUsername() );
        } else {
            throw new UsernameNotFoundException("Username not found in the database");
        }
    }

    @Override
    public Optional<User> getUser(String username) {
        log.debug("Retrieving user '{}'", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public Iterable<User> getUsers() {
        log.debug("Retrieving all users");
        return userRepository.findAll();
    }

    @Override
    public Iterable<User> saveAll(List<User> users) {
        return userRepository.saveAll(users);
    }
}
