package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.net.http.HttpHeaders;
import java.util.ArrayList;

public class JWTAuthenticationVerficationFilter extends BasicAuthenticationFilter {
    public JWTAuthenticationVerficationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    public UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
        String token = req.getHeader(SecurityConstants.HEADER_STRING);
        if (token != null) {
            JWTVerifier build = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes())).build();
            String user = build.verify(token.replace(SecurityConstants.TOKEN_PREFIX, "")).getSubject();
            if (user != null) return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            return null;
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(SecurityConstants.HEADER_STRING);
        if (header == header || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }
}
