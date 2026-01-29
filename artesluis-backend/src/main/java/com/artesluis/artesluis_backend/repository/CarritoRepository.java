package com.artesluis.artesluis_backend.repository;

import com.artesluis.artesluis_backend.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    
    Optional<Carrito> findBySessionId(String sessionId);
    
    void deleteBySessionId(String sessionId);
}