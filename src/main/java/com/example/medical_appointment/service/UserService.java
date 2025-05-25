package com.example.medical_appointment.service;

import com.example.medical_appointment.Models.Specialty;
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
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, SpecialtyRepository specialtyRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.specialtyRepository = specialtyRepository;
    }

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
    public User updateUser(Long userId, UserDTO userDTO, String userRole) {
        System.out.println("UserService.updateUser - Updating user ID: " + userId + ", role: " + userRole);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        System.out.println("UserService.updateUser - Found user: " + user.getEmail());

        if (!user.getRole().equalsIgnoreCase(userRole)) {
            throw new IllegalArgumentException("Role mismatch for user ID: " + userId);
        }

        if ("DOCTOR".equalsIgnoreCase(userRole)) {
            if (userDTO.getConsultationFee() != null) {
                user.setConsultationFee(userDTO.getConsultationFee());
                System.out.println("UserService.updateUser - Updated consultationFee: " + userDTO.getConsultationFee());
            }
            if (userDTO.getBiography() != null) {
                user.setBiography(userDTO.getBiography());
                System.out.println("UserService.updateUser - Updated biography: " + userDTO.getBiography());
            }
            if (userDTO.getSpecialtyId() != null) {
                Specialty specialty = specialtyRepository.findById(userDTO.getSpecialtyId())
                        .orElseThrow(() -> new IllegalArgumentException("Specialty not found with ID: " + userDTO.getSpecialtyId()));
                user.setSpecialty(specialty);
                System.out.println("UserService.updateUser - Updated specialty: " + specialty.getName());
            } else if (userDTO.getSpecialtyId() == null && userDTO.getConsultationFee() != null) {
                user.setSpecialty(null);
                System.out.println("UserService.updateUser - Removed specialty");
            }
            if (userDTO.getProfilePictureFile() != null && !userDTO.getProfilePictureFile().isEmpty()) {
                System.out.println("UserService.updateUser - Processing profile picture: " + userDTO.getProfilePictureFile().getOriginalFilename());
                String filePath = saveProfilePicture(userDTO.getProfilePictureFile());
                user.setProfilePicture(filePath);
                System.out.println("UserService.updateUser - Updated profile picture path: " + filePath);
            }
        } else if ("PATIENT".equalsIgnoreCase(userRole)) {
            if (userDTO.getProfilePictureFile() != null && !userDTO.getProfilePictureFile().isEmpty()) {
                System.out.println("UserService.updateUser - Processing profile picture: " + userDTO.getProfilePictureFile().getOriginalFilename());
                String filePath = saveProfilePicture(userDTO.getProfilePictureFile());
                user.setProfilePicture(filePath);
                System.out.println("UserService.updateUser - Updated profile picture path: " + filePath);
            }
        } else {
            throw new IllegalArgumentException("Invalid role: " + userRole);
        }

        User updatedUser = userRepository.save(user);
        System.out.println("UserService.updateUser - User saved successfully: " + updatedUser.getEmail());
        return updatedUser;
    }

public String saveProfilePicture(MultipartFile file) {
    if (file == null || file.isEmpty()) {
        System.out.println("UserService.saveProfilePicture - No file provided");
        return null;
    }
    long maxSize = 15 * 1024 * 1024; // 15 MB
    if (file.getSize() > maxSize) {
        System.out.println("UserService.saveProfilePicture - File size exceeds 15MB: " + file.getSize());
        throw new IllegalArgumentException("File size exceeds 15MB");
    }
    try {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get("src/main/resources/static/uploads/" + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        String filePath = "/uploads/" + fileName;
        System.out.println("UserService.saveProfilePicture - File saved: " + filePath);
        return filePath;
    } catch (IOException e) {
        System.out.println("UserService.saveProfilePicture - Failed to save file: " + e.getMessage());
        throw new IllegalArgumentException("Failed to save file: " + e.getMessage());
    }
}
}  