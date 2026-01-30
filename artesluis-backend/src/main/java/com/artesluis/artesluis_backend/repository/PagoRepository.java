package com.artesluis.artesluis_backend.repository;

import com.artesluis.artesluis_backend.model.Pago;
import com.artesluis.artesluis_backend.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    
    Optional<Pago> findByReferenciaPago(String referenciaPago);
    
    Optional<Pago> findByGatewayTransactionId(String gatewayTransactionId);
    
    List<Pago> findByOrdenOrderByFechaCreacionDesc(Orden orden);
    
    List<Pago> findByEstadoOrderByFechaCreacionDesc(Pago.EstadoPago estado);
    
    List<Pago> findByMetodoPagoOrderByFechaCreacionDesc(Pago.MetodoPago metodoPago);
    
    @Query("SELECT p FROM Pago p WHERE p.fechaCreacion >= :fechaInicio AND p.fechaCreacion <= :fechaFin ORDER BY p.fechaCreacion DESC")
    List<Pago> findByFechaCreacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                         @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT COUNT(p) FROM Pago p WHERE p.estado = :estado")
    Long countByEstado(@Param("estado") Pago.EstadoPago estado);
}