package com.example.medical_appointment.service;

import com.example.medical_appointment.Models.User;
import com.example.medical_appointment.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; // Changement ici : UserRepository au lieu de UserService

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email); // Changement ici : Utilisation directe de userRepository
        if (user == null) {
            throw new UsernameNotFoundException("Utilisateur introuvable : " + email);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}