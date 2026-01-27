package com.artesluis.artesluis_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    // Constructores
    public Rol() {
    }

        
    

    public Rol(String nombre) {
        
    
        this.nombre = nombre;
    }
        
    

    
        
    
    // Getters y Setters
    public Long getId() {
        
    

        return id;
        
    
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
