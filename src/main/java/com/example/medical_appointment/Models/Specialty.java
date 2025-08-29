package com.example.medical_appointment.Models;

<<<<<<< HEAD
import jakarta.persistence.*;

@Entity
@Table(name = "specialties")
public class Specialty {

=======

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "specialties")
public class Specialty {
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
    @Column(name = "name")
    private String name;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
=======
    @Column(name = "name", nullable = false, unique = true)
    private String name;
>>>>>>> 9ed9acb (Initiation du projet et le cahier de charge)
}