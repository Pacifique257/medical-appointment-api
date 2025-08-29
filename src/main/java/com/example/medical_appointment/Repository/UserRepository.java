package com.example.medical_appointment.Repository;

import com.example.medical_appointment.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(String role);
    User findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.id = :id")
    User findUserById(Long id); // Méthode personnalisée pour éviter Optional<User>
=======
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
}