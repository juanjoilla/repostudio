package com.artesluis.artesluis_backend.repository;

import com.artesluis.artesluis_backend.model.NotaOrden;
import com.artesluis.artesluis_backend.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaOrdenRepository extends JpaRepository<NotaOrden, Long> {
    
    List<NotaOrden> findByOrdenOrderByFechaCreacionDesc(Orden orden);
    
    List<NotaOrden> findByOrdenOrderByFechaCreacionAsc(Orden orden);
    
    Long countByOrden(Orden orden);
}
