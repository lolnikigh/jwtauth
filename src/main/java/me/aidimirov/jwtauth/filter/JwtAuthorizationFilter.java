package me.aidimirov.jwtauth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.aidimirov.jwtauth.config.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, ServletException {

        if (request.getServletPath().equals("/login") || request.getServletPath().equals("/user/token/refresh")) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    String token = authorizationHeader.substring(7);
                    String username = jwtService.getSubject(token);
                    String[] roles = jwtService.getAuthorities(token);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    Arrays.stream(roles).forEach(s -> authorities.add(new SimpleGrantedAuthority(s)));
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    log.error("Error logging in: {}", e.getMessage());
                    response.setHeader("error", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    Map<String, String> error = new HashMap<>();
                    error.put("error_message", e.getMessage());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }

            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
