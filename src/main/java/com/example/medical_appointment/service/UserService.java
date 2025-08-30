package com.example.medical_appointment.service;

import com.example.medical_appointment.Models.Specialty;
import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.Repository.SpecialtyRepository;
import com.example.medical_appointment.Repository.UserRepository;
import com.example.medical_appointment.dto.UserDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Base64;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB en octets
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void createUser(@Valid UserDTO userDTO) {
        // Restreindre la création d'ADMIN via l'endpoint public
        if ("ADMIN".equals(userDTO.getRole())) {
            throw new IllegalArgumentException("Le rôle ADMIN ne peut pas être créé via l'inscription publique");
        }
        createUserInternal(userDTO);
    }

    public void createInitialAdminUser(@Valid UserDTO userDTO) {
        // Vérifier qu'aucun compte admin n'existe
        List<User> admins = userRepository.findAll().stream()
                .filter(user -> "ADMIN".equals(user.getRole()))
                .toList();
        if (!admins.isEmpty()) {
            throw new IllegalArgumentException("Un compte admin existe déjà. Utilisez l'endpoint /api/v1/users pour créer d'autres admins.");
        }

        // Forcer le rôle à ADMIN
        userDTO.setRole("ADMIN");
        createUserInternal(userDTO);
    }

    public void createAdminUser(@Valid UserDTO userDTO) {
        createUserInternal(userDTO);
    }

    private void createUserInternal(@Valid UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        try {
            user.setBirthDate(userDTO.getBirthDate() != null ? LocalDate.parse(userDTO.getBirthDate(), DATE_FORMATTER) : null);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format de date invalide. Utilisez yyyy-MM-dd (ex. 1990-01-01)");
        }
        user.setAddress(userDTO.getAddress());
        user.setConsultationFee(userDTO.getConsultationFee());
        user.setBiography(userDTO.getBiography());
        user.setGender(userDTO.getGender());
        user.setRole(userDTO.getRole());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        if ("DOCTOR".equals(userDTO.getRole()) && userDTO.getSpecialtyId() != null) {
            Specialty specialty = specialtyRepository.findById(userDTO.getSpecialtyId())
                    .orElseThrow(() -> new IllegalArgumentException("Spécialité non trouvée"));
            user.setSpecialty(specialty);
        }

        MultipartFile image = userDTO.getProfilePictureFile();
        if (image != null && !image.isEmpty()) {
            if (image.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("La taille de la photo de profil ne doit pas dépasser 5 Mo");
            }
            try {
                byte[] bytes = image.getBytes();
                String base64Image = Base64.getEncoder().encodeToString(bytes);
                user.setProfilePicture(base64Image);
            } catch (IOException e) {
                throw new IllegalArgumentException("Erreur lors de la lecture du fichier de la photo de profil", e);
            }
        }

        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }

        userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateUser(Long id, UserDTO userDTO, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        // Restreindre la modification du rôle à ADMIN
        if (!role.equals("ADMIN") && !userDTO.getRole().equals(user.getRole())) {
            throw new IllegalArgumentException("Seul un ADMIN peut modifier le rôle d'un utilisateur");
        }

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        try {
            user.setBirthDate(userDTO.getBirthDate() != null ? LocalDate.parse(userDTO.getBirthDate(), DATE_FORMATTER) : null);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format de date invalide. Utilisez yyyy-MM-dd (ex. 1990-01-01)");
        }
        user.setAddress(userDTO.getAddress());
        user.setConsultationFee(userDTO.getConsultationFee());
        user.setBiography(userDTO.getBiography());
        user.setGender(userDTO.getGender());
        user.setRole(userDTO.getRole());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        if ("DOCTOR".equals(userDTO.getRole()) && userDTO.getSpecialtyId() != null) {
            Specialty specialty = specialtyRepository.findById(userDTO.getSpecialtyId())
                    .orElseThrow(() -> new IllegalArgumentException("Spécialité non trouvée"));
            user.setSpecialty(specialty);
        }

        MultipartFile image = userDTO.getProfilePictureFile();
        if (image != null && !image.isEmpty()) {
            if (image.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("La taille de la photo de profil ne doit pas dépasser 5 Mo");
            }
            try {
                byte[] bytes = image.getBytes();
                String base64Image = Base64.getEncoder().encodeToString(bytes);
                user.setProfilePicture(base64Image);
            } catch (IOException e) {
                throw new IllegalArgumentException("Erreur lors de la lecture du fichier de la photo de profil", e);
            }
        }

        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        userRepository.delete(user);
    }
}
