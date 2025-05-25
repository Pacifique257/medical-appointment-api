package com.example.medical_appointment.Repository;

import com.example.medical_appointment.Models.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    List<User> findByRoleAndSpecialtyId(String role, Long specialtyId);
     
}
