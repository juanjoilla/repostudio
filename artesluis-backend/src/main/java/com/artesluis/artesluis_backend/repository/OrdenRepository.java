package com.artesluis.artesluis_backend.repository;

import com.artesluis.artesluis_backend.model.Orden;
import com.artesluis.artesluis_backend.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    
    Optional<Orden> findByNumeroOrden(String numeroOrden);
    
    List<Orden> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);
    
    Page<Orden> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario, Pageable pageable);
    
    List<Orden> findByEstadoOrderByFechaCreacionDesc(Orden.EstadoOrden estado);
    
    Page<Orden> findByEstadoOrderByFechaCreacionDesc(Orden.EstadoOrden estado, Pageable pageable);
    
    Page<Orden> findAllByOrderByFechaCreacionDesc(Pageable pageable);
    
    @Query("SELECT o FROM Orden o WHERE o.fechaCreacion >= :fechaInicio AND o.fechaCreacion <= :fechaFin ORDER BY o.fechaCreacion DESC")
    List<Orden> findByFechaCreacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                          @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT COUNT(o) FROM Orden o WHERE o.estado = :estado")
    Long countByEstado(@Param("estado") Orden.EstadoOrden estado);
    
    @Query("SELECT SUM(o.total) FROM Orden o WHERE o.estado = 'PAGADO' OR o.estado = 'COMPLETADO'")
    BigDecimal getTotalVentas();
    
    @Query("SELECT SUM(o.total) FROM Orden o WHERE o.estado IN ('PAGADO', 'COMPLETADO') " +
           "AND o.fechaCreacion >= :fechaInicio AND o.fechaCreacion <= :fechaFin")
    BigDecimal getTotalVentasPorPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                       @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT COUNT(o) FROM Orden o WHERE o.fechaCreacion >= :fechaInicio AND o.fechaCreacion <= :fechaFin")
    Long contarOrdenesPorPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                @Param("fechaFin") LocalDateTime fechaFin);
    
    // Para búsquedas del administrador
    @Query("SELECT o FROM Orden o WHERE " +
           "(:numeroOrden IS NULL OR o.numeroOrden LIKE %:numeroOrden%) AND " +
           "(:estado IS NULL OR o.estado = :estado) AND " +
           "(:emailCliente IS NULL OR o.emailCliente LIKE %:emailCliente%) " +
           "ORDER BY o.fechaCreacion DESC")
    Page<Orden> findOrdersWithFilters(@Param("numeroOrden") String numeroOrden,
                                     @Param("estado") Orden.EstadoOrden estado,
                                     @Param("emailCliente") String emailCliente,
                                     Pageable pageable);
}