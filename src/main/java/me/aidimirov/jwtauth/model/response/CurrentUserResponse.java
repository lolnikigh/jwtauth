package me.aidimirov.jwtauth.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.aidimirov.jwtauth.model.User;

@Getter
@Setter
@AllArgsConstructor
public class CurrentUserResponse {

    private String firstname;
    private String lastname;
    private String username;

    public CurrentUserResponse(User user) {
        this(user.getFirstname(), user.getLastname(), user.getUsername());
    }
}
