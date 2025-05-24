package com.example.medical_appointment.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;import java.io.IOException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;     

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestURL = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        System.out.println("Processing request for URL: " + requestURL);

        String jwt = null;
        String email = null;

        // Check Authorization header
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            System.out.println("JWT extracted from Authorization header: " + jwt);
        }

        // Check URL parameter
        if (jwt == null) {
            String tokenParam = request.getParameter("token");
            if (tokenParam != null && !tokenParam.isEmpty()) {
                // Clean token: Take only the first token if multiple are concatenated
                jwt = tokenParam.contains(",") ? tokenParam.split(",")[0] : tokenParam;
                System.out.println("JWT extracted from URL parameter: " + jwt);
            }
        }

        // Check session attribute (unlikely with stateless config)
        if (jwt == null) {
            Object tokenFromSession = request.getAttribute("jwtTokenFromSession");
            if (tokenFromSession instanceof String tokenStr) {
                jwt = tokenStr;
                System.out.println("JWT extracted from session attribute: " + jwt);
            } else {
                System.out.println("No JWT found in Authorization header, URL parameter, or session for URL: " + requestURL);
            }
        }

        // Check blacklist
        if (jwt != null && tokenBlacklist.isBlacklisted(jwt)) {
            System.out.println("Token is blacklisted: " + jwt);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
            return;
        }

        // Validate token
        if (jwt != null) {
            try {
                email = jwtUtil.extractEmail(jwt);
                System.out.println("Extracted email from JWT: " + email);
                boolean isValid = jwtUtil.validateToken(jwt, email);
                System.out.println("Token validation result for email " + email + ": " + isValid);
                if (!isValid) {
                    System.out.println("Token validation failed for email " + email + ": Invalid or expired token");
                }
            } catch (Exception e) {
                System.out.println("Error extracting email or validating token: " + e.getMessage());
                email = null;
            }
        }

        // Authenticate if valid
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (userDetails == null) {
                System.out.println("UserDetails not found for email: " + email);
            } else if (jwtUtil.validateToken(jwt, email)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                System.out.println("Authentication successful for email: " + email + ", authorities: " + userDetails.getAuthorities());
            } else {
                System.out.println("Invalid token for email: " + email);
            }
        }

        chain.doFilter(request, response);
    }
}
