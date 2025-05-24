package com.example.medical_appointment.controller;


import com.example.medical_appointment.Models.LoginRequest;
import com.example.medical_appointment.dto.UserDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class WebController {

    @Autowired
    private RestTemplate restTemplate;

    // Page de login à la racine
    @GetMapping("/")
    public String loginForm(Model model) {
        model.addAttribute("credentials", new LoginRequest());
        return "login";
    }

    // Redirection GET /login vers la racine (page login)
    @GetMapping("/login")
    public String loginRedirect() {
        return "redirect:/";
    }


   @GetMapping("/home-doctor")
    public String homeDoctor(HttpSession session, Model model) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            String token = (String) session.getAttribute("accessToken");

            if (userId == null || token == null) {
                model.addAttribute("error", "User not authenticated");
                System.out.println("Unauthorized access to home-doctor: userId or token is null");
                return "redirect:/login";
            }

            // Log the token for debugging
            System.out.println("Token retrieved from session: " + token);

            // Call /api/users/id/{id} to fetch user details
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "http://localhost:8080/api/users/id/" + userId,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> user = response.getBody();
                model.addAttribute("firstName", user.get("firstName"));
                model.addAttribute("lastName", user.get("lastName"));
                model.addAttribute("user", user);
                model.addAttribute("token", token); // Pass token to the model
                System.out.println("User retrieved for home-doctor: " + user);
                return "home-doctor";
            } else {
                model.addAttribute("error", "Unable to retrieve user information");
                System.out.println("Failed to retrieve user info for userId: " + userId);
                return "redirect:/login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            System.out.println("Error in homeDoctor: " + e.getMessage());
            return "redirect:/login";
        }
    }


       @GetMapping("/home-patient")
    public String homePatient(HttpSession session, Model model) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            String token = (String) session.getAttribute("accessToken");

            if (userId == null || token == null) {
                model.addAttribute("error", "User not authenticated");
                System.out.println("Unauthorized access to home-patient: userId or token is null");
                return "redirect:/login";
            }

            // Log the token for debugging
            System.out.println("Token retrieved from session: " + token);

            // Call /api/users/id/{id} to fetch user details
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "http://localhost:8080/api/users/id/" + userId,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> user = response.getBody();
                model.addAttribute("firstName", user.get("firstName"));
                model.addAttribute("lastName", user.get("lastName"));
                model.addAttribute("user", user);
                model.addAttribute("token", token); // Pass token to the model
                System.out.println("User retrieved for home-patient: " + user);
                return "home-patient";
            } else {
                model.addAttribute("error", "Unable to retrieve user information");
                System.out.println("Failed to retrieve user info for userId: " + userId);
                return "redirect:/login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            System.out.println("Error in homePatient: " + e.getMessage());
            return "redirect:/login";
        }
    }
    
    
    @GetMapping("/home")
    public String homeGeneric() {
        return "home";
    }

@PostMapping("/login")
public String loginSubmit(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
    try {
        Map<String, String> credentials = Map.of("email", email, "password", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(credentials, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            "http://localhost:8080/api/auth/login",
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<>() {}
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            String token = (String) body.get("accessToken");
            Integer userId = (Integer) body.get("userId");
            String role = (String) body.get("role");

            // Store in session for Thymeleaf (optional)
            session.setAttribute("accessToken", token);
            session.setAttribute("userId", userId);
            session.setAttribute("userRole", role);

            // Set the security context
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
            System.out.println("Security context set for email: " + email + ", role: ROLE_" + role);

            // Redirect with the token as a parameter
            if ("DOCTOR".equalsIgnoreCase(role)) {
                return "redirect:/home-doctor?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
            } else if ("PATIENT".equalsIgnoreCase(role)) {
                return "redirect:/home-patient?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
            } else {
                model.addAttribute("error", "Unknown user role: " + role);
                return "login";
            }
        } else {
            model.addAttribute("error", response.getBody() != null ? response.getBody().get("error") : "Login failed");
            return "login";
        }
    } catch (Exception e) {
        model.addAttribute("error", "Login failed: " + e.getMessage());
        return "login";
    }
}



    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute("userDTO") UserDTO userDTO, Model model) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<UserDTO> requestEntity = new HttpEntity<>(userDTO, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:8080/api/users",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("message", "User created successfully");
                return "redirect:/";
            } else {
                model.addAttribute("error", response.getBody() != null ? response.getBody().get("error") : "Registration failed");
                return "register";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/specialties")
    public String specialties(Model model) {
        try {
            ResponseEntity<Object[]> response = restTemplate.getForEntity(
                "http://localhost:8080/api/specialties",
                Object[].class
            );
            model.addAttribute("specialties", response.getBody());
            return "specialties";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load specialties: " + e.getMessage());
            return "specialties";
        }
    }

@GetMapping("/logout")
public String logout(HttpSession session, HttpServletResponse response, HttpServletRequest request, Model model) {
    try {
        String token = (String) session.getAttribute("accessToken");

        if (token != null) {
            // Call the /api/auth/logout API to blacklist the token
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> logoutRequest = new HttpEntity<>(headers);

            ResponseEntity<Map<String, String>> logoutResponse = restTemplate.exchange(
                "http://localhost:8080/api/auth/logout",
                HttpMethod.POST,
                logoutRequest,
                new ParameterizedTypeReference<>() {}
            );

            if (!logoutResponse.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("error", "Error during logout");
                System.out.println("Error while calling /api/auth/logout: " + logoutResponse.getBody());
            }
        }

        // Invalidate the session
        session.invalidate();

        // Delete the jwtToken cookie
        Cookie tokenCookie = new Cookie("jwtToken", null);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setSecure(false); // For local testing without HTTPS
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(0); // Deletes the cookie
        response.addCookie(tokenCookie);

        // Clear the security context
        SecurityContextHolder.clearContext();

        System.out.println("Logout successful for the user");
        return "redirect:/login";
    } catch (Exception e) {
        model.addAttribute("error", "Error during logout: " + e.getMessage());
        System.out.println("Error in logout: " + e.getMessage());
        return "redirect:/login";
    }
}
}
