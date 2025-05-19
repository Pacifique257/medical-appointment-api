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
       import jakarta.servlet.http.HttpServletResponse;
       import java.io.IOException;

       @Component
       public class JwtRequestFilter extends OncePerRequestFilter {

           @Autowired
           private JwtUtil jwtUtil;

           @Autowired
           private TokenBlacklist tokenBlacklist;

           @Override
           protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                   throws ServletException, IOException {
               final String authorizationHeader = request.getHeader("Authorization");

               String email = null;
               String jwt = null;

               if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                   jwt = authorizationHeader.substring(7);
                   if (tokenBlacklist.isBlacklisted(jwt)) {
                       response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
                       return;
                   }
                   email = jwtUtil.extractEmail(jwt);
               }

               if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                   if (jwtUtil.validateToken(jwt, email)) {
                       UsernamePasswordAuthenticationToken authenticationToken =
                               new UsernamePasswordAuthenticationToken(email, null, null);
                       authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                       SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                   }
               }
               chain.doFilter(request, response);
           }
       }