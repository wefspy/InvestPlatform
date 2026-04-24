package com.example.investplatform.infrastructure.security.filter;

import com.example.investplatform.infrastructure.config.property.SecurityTitleConstants;
import com.example.investplatform.infrastructure.security.jwt.parser.AccessTokenParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String headerName = "Authorization";
    private final String headerStartValue = "Bearer ";

    private final AccessTokenParser accessTokenParser;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(AccessTokenParser accessTokenParser,
                                   UserDetailsService userDetailsService) {
        this.accessTokenParser = accessTokenParser;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);
        if (!isAccessToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        authenticateRequest(token, request);
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(headerName);
        if (authHeader == null || !authHeader.startsWith(headerStartValue)) {
            return null;
        }

        return authHeader.substring(headerStartValue.length());
    }

    private boolean isAccessToken(String token) {
        return accessTokenParser.isValid(token) && accessTokenParser.ofType(token);
    }

    private void authenticateRequest(String token, HttpServletRequest request) {
        String username = accessTokenParser.getUsername(token);
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
