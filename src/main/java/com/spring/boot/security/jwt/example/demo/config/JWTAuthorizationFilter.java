package com.spring.boot.security.jwt.example.demo.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.CollectionUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final JWTProperties jwtProperties;

    JWTAuthorizationFilter(AuthenticationManager authManager, JWTProperties jwtProperties) {
        super(authManager);
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(jwtProperties.getHeaderString());

        if (header == null || !header.startsWith(jwtProperties.getTokenPrefix())) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(jwtProperties.getHeaderString());
        if (token != null) {
            // parse the token.
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(jwtProperties.getSecret()))
                    .build()
                    .verify(token.replace(jwtProperties.getTokenPrefix(), ""));

            String user = decodedJWT.getSubject();
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, getGrantedAuthorities(decodedJWT));
            }
            return null;
        }
        return null;
    }

    private List<GrantedAuthority> getGrantedAuthorities(DecodedJWT decodedJWT) {
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
        List<GrantedAuthority> grantedAuthorities = Collections.emptyList();
        if (!CollectionUtils.isEmpty(roles)) {
            grantedAuthorities = roles.stream().map(SimpleGrantedAuthority::new).collect(toList());
        }
        return grantedAuthorities;
    }
}
