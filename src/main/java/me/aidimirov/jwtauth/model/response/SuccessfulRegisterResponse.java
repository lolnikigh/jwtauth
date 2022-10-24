package me.aidimirov.jwtauth.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.aidimirov.jwtauth.model.User;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
public class SuccessfulRegisterResponse {

    private String firstname;
    private String lastname;
    private String username;
    private Date createdAt;

    public SuccessfulRegisterResponse(User user) {
        this(user.getFirstname(), user.getLastname(), user.getUsername(), user.getCreatedAt());
    }

}