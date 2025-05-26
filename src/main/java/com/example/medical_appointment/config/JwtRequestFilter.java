package com.example.medical_appointment.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklist tokenBlacklist;

    public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService, TokenBlacklist tokenBlacklist) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestURL = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        System.out.println("Processing request for URL: " + requestURL);

        String jwt = null;
        String email = null;

        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            System.out.println("JWT extracted from Authorization header: " + jwt);
        }

        if (jwt == null) {
            String tokenParam = request.getParameter("token");
            if (tokenParam != null && !tokenParam.isEmpty()) {
                jwt = tokenParam.contains(",") ? tokenParam.split(",")[0] : tokenParam;
                System.out.println("JWT extracted from URL parameter: " + jwt);
            }
        }

        if (jwt == null) {
            Object tokenFromSession = request.getAttribute("jwtTokenFromSession");
            if (tokenFromSession instanceof String tokenStr) {
                jwt = tokenStr;
                System.out.println("JWT extracted from session attribute: " + jwt);
            } else {
                System.out.println("No JWT found in Authorization header, URL parameter, or session for URL: " + requestURL);
            }
        }

        if (jwt != null && tokenBlacklist.isBlacklisted(jwt)) {
            System.out.println("Token is blacklisted: " + jwt);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
            return;
        }

        if (jwt != null) {
            try {
                email = jwtUtil.extractEmail(jwt);
                System.out.println("Extracted email from JWT: " + email);
                boolean isValid = jwtUtil.validateToken(jwt, email);
                System.out.println("Token validation result for email " + email + ": " + isValid);

                if (!isValid) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Error extracting email or validating token: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (userDetails == null) {
                System.out.println("UserDetails not found for email: " + email);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            }
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            System.out.println("Authentication successful for email: " + email + ", authorities: " + userDetails.getAuthorities());
        }

        chain.doFilter(request, response);
    }
}
