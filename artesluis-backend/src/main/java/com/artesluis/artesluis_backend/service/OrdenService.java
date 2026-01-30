package com.artesluis.artesluis_backend.service;

import com.artesluis.artesluis_backend.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdenService {
    
    // Operaciones básicas de CRUD
    Orden crearOrden(Usuario usuario, List<ItemCarrito> items);
    
    Optional<Orden> obtenerPorId(Long id);
    
    Optional<Orden> obtenerPorNumeroOrden(String numeroOrden);
    
    Orden guardarOrden(Orden orden);
    
    void eliminarOrden(Long id);
    
    // Consultas de usuario
    List<Orden> obtenerOrdenesPorUsuario(Usuario usuario);
    
    Page<Orden> obtenerOrdenesPorUsuario(Usuario usuario, Pageable pageable);
    
    // Consultas administrativas
    Page<Orden> obtenerTodasLasOrdenes(Pageable pageable);
    
    Page<Orden> obtenerOrdenesPorEstado(Orden.EstadoOrden estado, Pageable pageable);
    
    Page<Orden> buscarOrdenes(String numeroOrden, Orden.EstadoOrden estado, String emailCliente, Pageable pageable);
    
    // Estadísticas
    Long contarOrdenes();
    
    Long contarOrdenesPorEstado(Orden.EstadoOrden estado);
    
    BigDecimal obtenerTotalVentas();
    
    BigDecimal obtenerTotalVentasPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    Long contarOrdenesPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // Operaciones de estado
    Orden actualizarEstado(Long ordenId, Orden.EstadoOrden nuevoEstado);
    
    Orden procesarPago(Long ordenId, Pago.MetodoPago metodoPago, String referenciaPago);
    
    // Validaciones
    boolean puedeModificarOrden(Long ordenId);
    
    boolean puedeReembolsarOrden(Long ordenId);
}