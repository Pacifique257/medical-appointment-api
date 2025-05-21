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
        String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;
        String email = null;

        // Extract JWT from Authorization header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
        }

        // Check if token is blacklisted
        if (jwt != null && tokenBlacklist.isBlacklisted(jwt)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
            return;
        }

        // Extract email from token
        if (jwt != null) {
            try {
                email = jwtUtil.extractEmail(jwt);
                logger.info("Extracted email from JWT: " + email);
            } catch (Exception e) {
                logger.error("Error extracting email from token: " + e.getMessage());
            }
        }

        // Authenticate if email is extracted and no existing authentication
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (jwtUtil.validateToken(jwt, email)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                logger.info("Authentication successful for email: " + email + ", authorities: " + userDetails.getAuthorities());
            } else {
                logger.warn("Invalid token for email: " + email);
            }
        }

        chain.doFilter(request, response);
    }
}