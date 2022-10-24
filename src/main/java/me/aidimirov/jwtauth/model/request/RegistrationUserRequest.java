package me.aidimirov.jwtauth.model.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegistrationUserRequest {

    private final String firstname;
    private final String lastname;
    private final String username;
    private final String password;
    private final String email;

}