package me.aidimirov.jwtauth.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtConfig {

    @Value("${auth.jwt.secret}")
    private String secret;

    @Value("${auth.jwt.expiration.access-token}")
    private Long expirationAccessToken;

    @Value("${auth.jwt.expiration.refresh-token}")
    private Long expirationRefreshToken;
}