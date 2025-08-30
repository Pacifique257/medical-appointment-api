package com.example.medical_appointment.controller;

import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.dto.UserDTO;
import com.example.medical_appointment.service.UserService;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Operation(summary = "Inscrire un utilisateur", description = "Crée un nouvel utilisateur. Accessible à tous.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Utilisateur inscrit avec succès"),
        @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
        @ApiResponse(responseCode = "429", description = "Limite de débit dépassée")
    })
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

    @Operation(summary = "Inscrire un admin initial", description = "Crée un utilisateur admin initial. Accessible à tous.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Admin initial créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
        @ApiResponse(responseCode = "429", description = "Limite de débit dépassée")
    })
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

    @Operation(summary = "Créer un utilisateur", description = "Crée un utilisateur. Réservé aux ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
        @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé"),
        @ApiResponse(responseCode = "429", description = "Limite de débit dépassée")
    })
    @PostMapping(consumes = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO) {
        if (!rateLimitBucket.tryConsume(1)) {
            logger.warn("Limite de débit dépassée pour la création d'utilisateur");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Limite de débit dépassée"));
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

    @Operation(summary = "Récupérer un utilisateur par ID", description = "Récupère un utilisateur par son ID. Accessible à l'utilisateur lui-même ou à un ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur récupéré avec succès"),
        @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "429", description = "Limite de débit dépassée")
    })
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

    @Operation(summary = "Lister tous les utilisateurs", description = "Récupère la liste de tous les utilisateurs. Réservé aux ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée avec succès"),
        @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé"),
        @ApiResponse(responseCode = "429", description = "Limite de débit dépassée")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        if (!rateLimitBucket.tryConsume(1)) {
            logger.warn("Limite de débit dépassée pour la récupération de tous les utilisateurs");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Limite de débit dépassée"));
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

    @Operation(summary = "Mettre à jour un utilisateur", description = "Met à jour un utilisateur par son ID. Accessible à l'utilisateur lui-même ou à un ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès"),
        @ApiResponse(responseCode = "400", description = "Données d'entrée invalides"),
        @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "429", description = "Limite de débit dépassée")
    })
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

    @Operation(summary = "Supprimer un utilisateur", description = "Supprime un utilisateur par son ID. Réservé aux ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur supprimé avec succès"),
        @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "429", description = "Limite de débit dépassée")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!rateLimitBucket.tryConsume(1)) {
            logger.warn("Limite de débit dépassée pour la suppression de l'utilisateur ID : {}", id);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Limite de débit dépassée"));
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

    @Operation(summary = "Uploader une photo de profil", description = "Met à jour la photo de profil d'un utilisateur. Accessible à l'utilisateur lui-même ou à un ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Photo de profil mise à jour avec succès"),
        @ApiResponse(responseCode = "400", description = "Fichier invalide ou données invalides"),
        @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé"),
        @ApiResponse(responseCode = "429", description = "Limite de débit dépassée")
    })
    @PostMapping(value = "/{id}/profile-picture", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        if (!rateLimitBucket.tryConsume(1)) {
            logger.warn("Limite de débit dépassée pour l'upload de la photo de profil, ID : {}", id);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Limite de débit dépassée"));
        }
        if (!file.getContentType().startsWith("image/") || file.getSize() > 5 * 1024 * 1024) { // 5MB max
            logger.warn("Fichier invalide: type={} ou taille={}", file.getContentType(), file.getSize());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Fichier invalide (doit être une image, max 5MB)"));
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
