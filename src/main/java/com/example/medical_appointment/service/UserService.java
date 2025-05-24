package com.example.medical_appointment.service;

import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.Repository.SpecialtyRepository;
import com.example.medical_appointment.Repository.UserRepository;
import com.example.medical_appointment.dto.UserDTO;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import java.nio.file.Path; // ✅ Correct
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Paths;




@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SpecialtyRepository specialtyRepository;
      // Base path for storing uploaded files
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,SpecialtyRepository specialtyRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
         this.specialtyRepository = specialtyRepository;
    }
@Autowired
private SpecialtyRepository specialty;

    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
       // Updates user details based on role
    public User updateUser(Long userId, UserDTO updateDTO, String userRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (!user.getRole().equalsIgnoreCase(userRole)) {
            throw new IllegalArgumentException("Role mismatch for user ID: " + userId);
        }

        if ("DOCTOR".equalsIgnoreCase(userRole)) {
            if (updateDTO.getConsultationFee() != null) {
                user.setConsultationFee(updateDTO.getConsultationFee());
            }
            if (updateDTO.getBiography() != null) {
                user.setBiography(updateDTO.getBiography());
            }
            if (updateDTO.getSpecialtyId() != null) {
                var specialty = specialtyRepository.findById(updateDTO.getSpecialtyId())
                        .orElseThrow(() -> new IllegalArgumentException("Specialty not found with ID: " + updateDTO.getSpecialtyId()));
                user.setSpecialty(specialty);
            } else if (updateDTO.getSpecialtyId() == null && updateDTO.getConsultationFee() != null) {
                user.setSpecialty(null); // Permet de supprimer la spécialité
            }
            if (updateDTO.getProfilePictureFile() != null && !updateDTO.getProfilePictureFile().isEmpty()) {
                String filePath = saveProfilePicture(updateDTO.getProfilePictureFile());
                user.setProfilePicture(filePath);
            }
        } else if ("PATIENT".equalsIgnoreCase(userRole)) {
            if (updateDTO.getProfilePictureFile() != null && !updateDTO.getProfilePictureFile().isEmpty()) {
                String filePath = saveProfilePicture(updateDTO.getProfilePictureFile());
                user.setProfilePicture(filePath);
            }
        } else {
            throw new IllegalArgumentException("Invalid role: " + userRole);
        }

        return userRepository.save(user);
    }

private String saveProfilePicture(MultipartFile file) {
    try {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid file type. Only images are allowed.");
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        return "/uploads/" + fileName;
    } catch (IOException e) {
        throw new IllegalArgumentException("Failed to save profile picture: " + e.getMessage());
    }
}

}
    
    



