package com.artesluis.artesluis_backend.repository;

import com.artesluis.artesluis_backend.model.DetalleOrden;
import com.artesluis.artesluis_backend.model.Orden;
import com.artesluis.artesluis_backend.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {
    
    List<DetalleOrden> findByOrden(Orden orden);
    
    List<DetalleOrden> findByPlan(Plan plan);
    
    @Query("SELECT d FROM DetalleOrden d WHERE d.orden.fechaCreacion >= :fechaInicio AND d.orden.fechaCreacion <= :fechaFin")
    List<DetalleOrden> findByFechaCreacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                                 @Param("fechaFin") LocalDateTime fechaFin);
    
    // Estadísticas para el administrador
    @Query("SELECT d.plan, SUM(d.cantidad) FROM DetalleOrden d " +
           "WHERE d.orden.estado IN ('PAGADO', 'COMPLETADO') " +
           "GROUP BY d.plan ORDER BY SUM(d.cantidad) DESC")
    List<Object[]> findPlanesVendidosPorCantidad();
    
    @Query("SELECT d.plan, COUNT(d) FROM DetalleOrden d " +
           "WHERE d.orden.estado IN ('PAGADO', 'COMPLETADO') " +
           "GROUP BY d.plan ORDER BY COUNT(d) DESC")
    List<Object[]> findPlanesVendidosPorOrdenes();
}