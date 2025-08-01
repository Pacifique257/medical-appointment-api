package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.dto.UserDTO;
import com.example.medical_appointment.service.UserService;
import io.github.bucket4j.Bucket;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final Bucket rateLimitBucket;

    @Autowired
    public UserController(UserService userService, Bucket rateLimitBucket) {
        this.userService = userService;
        this.rateLimitBucket = rateLimitBucket;
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        if (!rateLimitBucket.tryConsume(1)) {
            logger.warn("Limite de débit dépassée pour l'inscription d'utilisateur");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Limite de débit dépassée"));
        }
        logger.info("Inscription de l'utilisateur : {}", userDTO.getEmail());
        try {
            userService.createUser(userDTO);
            logger.info("Utilisateur inscrit avec succès : {}", userDTO.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Utilisateur inscrit avec succès"));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de l'inscription de l'utilisateur : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/admin/register", consumes = "application/json")
    public ResponseEntity<?> registerInitialAdmin(@Valid @RequestBody UserDTO userDTO) {
        if (!rateLimitBucket.tryConsume(1)) {
            logger.warn("Limite de débit dépassée pour l'inscription d'un admin initial");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Limite de débit dépassée"));
        }
        logger.info("Inscription de l'admin initial : {}", userDTO.getEmail());
        try {
            userService.createInitialAdminUser(userDTO);
            logger.info("Admin initial inscrit avec succès : {}", userDTO.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Admin initial créé avec succès"));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de l'inscription de l'admin initial : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO) {
        if (!rateLimitBucket.tryConsume(1)) {
            logger.warn("Limite de débit dépassée pour la création d'utilisateur");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Limite de débit dépassée"));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            logger.warn("Utilisateur non authentifié pour la création d'utilisateur");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Utilisateur non authentifié"));
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User authenticatedUser = userService.getUserByEmail(userDetails.getUsername());
        if (authenticatedUser == null || !authenticatedUser.getRole().equals("ADMIN")) {
            logger.warn("Accès non autorisé pour l'utilisateur : {}", userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Seul un ADMIN peut créer des utilisateurs"));
        }

        logger.info("Création de l'utilisateur : {}", userDTO.getEmail());
        try {
            userService.createAdminUser(userDTO);
            logger.info("Utilisateur créé avec succès : {}", userDTO.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Utilisateur créé avec succès"));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la création de l'utilisateur : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        if (!rateLimitBucket.tryConsume(1)) {
            logger.warn("Limite de débit dépassée pour la récupération de l'utilisateur ID : {}", id);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Limite de débit dépassée"));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            logger.warn("Utilisateur non authentifié");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Utilisateur non authentifié"));
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User authenticatedUser = userService.getUserByEmail(userDetails.getUsername());
        if (authenticatedUser == null || (!authenticatedUser.getId().equals(id) && !authenticatedUser.getRole().equals("ADMIN"))) {
            logger.warn("Accès non autorisé pour l'utilisateur : {}", userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Accès non autorisé"));
        }

        try {
            User user = userService.getUserById(id);
            logger.info("Utilisateur récupéré avec succès : ID {}", id);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la récupération de l'utilisateur : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        if (!rateLimitBucket.tryConsume(1)) {
            logger.warn("Limite de débit dépassée pour la récupération de tous les utilisateurs");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Limite de débit dépassée"));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            logger.warn("Utilisateur non authentifié");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Utilisateur non authentifié"));
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User authenticatedUser = userService.getUserByEmail(userDetails.getUsername());
        if (authenticatedUser == null || !authenticatedUser.getRole().equals("ADMIN")) {
            logger.warn("Accès non autorisé pour l'utilisateur : {}", userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Seul un ADMIN peut lister tous les utilisateurs"));
        }

        try {
            List<User> users = userService.getAllUsers();
            logger.info("Liste des utilisateurs récupérée avec succès");
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la récupération des utilisateurs : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        if (!rateLimitBucket.tryConsume(1)) {
            logger.warn("Limite de débit dépassée pour la mise à jour de l'utilisateur ID : {}", id);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Limite de débit dépassée"));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            logger.warn("Utilisateur non authentifié");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Utilisateur non authentifié"));
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User authenticatedUser = userService.getUserByEmail(userDetails.getUsername());
        if (authenticatedUser == null || (!authenticatedUser.getId().equals(id) && !authenticatedUser.getRole().equals("ADMIN"))) {
            logger.warn("Accès non autorisé pour l'utilisateur : {}", userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Accès non autorisé"));
        }

        try {
            userService.updateUser(id, userDTO, authenticatedUser.getRole());
            logger.info("Utilisateur mis à jour avec succès : {}", userDTO.getEmail());
            return ResponseEntity.ok(Map.of("message", "Utilisateur mis à jour avec succès"));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la mise à jour de l'utilisateur : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!rateLimitBucket.tryConsume(1)) {
            logger.warn("Limite de débit dépassée pour la suppression de l'utilisateur ID : {}", id);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Limite de débit dépassée"));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            logger.warn("Utilisateur non authentifié");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Utilisateur non authentifié"));
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User authenticatedUser = userService.getUserByEmail(userDetails.getUsername());
        if (authenticatedUser == null || !authenticatedUser.getRole().equals("ADMIN")) {
            logger.warn("Accès non autorisé pour l'utilisateur : {}", userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Seul un ADMIN peut supprimer des utilisateurs"));
        }

        try {
            userService.deleteUser(id);
            logger.info("Utilisateur supprimé avec succès : ID {}", id);
            return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès"));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de la suppression de l'utilisateur : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/{id}/profile-picture", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        if (!rateLimitBucket.tryConsume(1)) {
            logger.warn("Limite de débit dépassée pour l'upload de la photo de profil, ID : {}", id);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Limite de débit dépassée"));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            logger.warn("Utilisateur non authentifié");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Utilisateur non authentifié"));
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User authenticatedUser = userService.getUserByEmail(userDetails.getUsername());
        if (authenticatedUser == null || (!authenticatedUser.getId().equals(id) && !authenticatedUser.getRole().equals("ADMIN"))) {
            logger.warn("Accès non autorisé pour l'utilisateur : {}", userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Accès non autorisé"));
        }

        try {
            UserDTO userDTO = new UserDTO();
            userDTO.setProfilePictureFile(file);
            userService.updateUser(id, userDTO, authenticatedUser.getRole());
            logger.info("Photo de profil mise à jour pour l'utilisateur ID : {}", id);
            return ResponseEntity.ok(Map.of("message", "Photo de profil mise à jour avec succès"));
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de l'upload de la photo de profil : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
