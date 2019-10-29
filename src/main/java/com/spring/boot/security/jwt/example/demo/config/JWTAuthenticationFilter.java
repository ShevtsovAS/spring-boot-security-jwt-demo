package com.spring.boot.security.jwt.example.demo.config;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.boot.security.jwt.example.demo.model.users.ApplicationUser;
import com.spring.boot.security.jwt.example.demo.model.users.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.spring.boot.security.jwt.example.demo.util.DateTimeUtil.getDate;
import static java.time.LocalDateTime.now;

@RequiredArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JWTProperties jwtProperties;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            ApplicationUser user = new ObjectMapper().readValue(req.getInputStream(), ApplicationUser.class);
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            user.getPassword(),
                            new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String[] roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);

        String token = JWT.create()
                .withSubject(principal.getUsername())
                .withArrayClaim("roles", roles)
                .withExpiresAt(getDate(now().plusHours(jwtProperties.getExpirationTime())))
                .sign(HMAC512(jwtProperties.getSecret()));
        res.addHeader(jwtProperties.getHeaderString(), jwtProperties.getTokenPrefix() + token);
    }
}
