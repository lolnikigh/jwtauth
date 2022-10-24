package me.aidimirov.jwtauth.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Autowired
    private JwtConfig config;

    public String getSubject(String token) {
        return decodedToken(token).getSubject();
    }

    public String[] getAuthorities(String token) {
        return decodedToken(token).getClaim("authorities").asArray(String.class);
    }

    public String accessToken(UserDetails user, HttpServletRequest request) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60000 * config.getExpirationAccessToken()))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("authorities", user.getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm());
    }

    public String refreshToken(UserDetails user, HttpServletRequest request) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60000 * config.getExpirationRefreshToken()))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm());
    }

    private DecodedJWT decodedToken(String token) {
        return JWT.require(algorithm()).build()
                .verify(token);
    }

    private Algorithm algorithm() {
        return Algorithm.HMAC512(config.getSecret().getBytes());
    }
}

