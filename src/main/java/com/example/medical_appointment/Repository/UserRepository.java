package com.example.medical_appointment.Repository;

import com.example.medical_appointment.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(String role);
    User findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.id = :id")
    User findUserById(Long id); // Méthode personnalisée pour éviter Optional<User>
}