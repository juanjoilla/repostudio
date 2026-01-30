package com.artesluis.artesluis_backend.service;

import com.artesluis.artesluis_backend.model.*;
import com.artesluis.artesluis_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrdenServiceImpl implements OrdenService {
    
    @Autowired
    private OrdenRepository ordenRepository;
    
    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;
    
    @Autowired
    private PagoRepository pagoRepository;
    
    @Autowired
    private CarritoRepository carritoRepository;
    
    @Override
    public Orden crearOrden(Usuario usuario, List<ItemCarrito> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("No se pueden crear órdenes sin items");
        }
        
        // Crear la orden
        Orden orden = new Orden(usuario, BigDecimal.ZERO);
        orden = ordenRepository.save(orden);
        
        // Crear los detalles de la orden
        List<DetalleOrden> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        
        for (ItemCarrito item : items) {
            DetalleOrden detalle = new DetalleOrden(orden, item.getPlan(), 
                    item.getCantidad(), item.getPrecioUnitario());
            detalles.add(detalle);
            total = total.add(detalle.calcularSubtotal());
        }
        
        detalleOrdenRepository.saveAll(detalles);
        
        // Actualizar el total de la orden
        orden.setDetalles(detalles);
        orden.setSubtotal(total);
        orden.setTotal(total);
        
        return ordenRepository.save(orden);
    }
    
    @Override
    public Optional<Orden> obtenerPorId(Long id) {
        return ordenRepository.findById(id);
    }
    
    @Override
    public Optional<Orden> obtenerPorNumeroOrden(String numeroOrden) {
        return ordenRepository.findByNumeroOrden(numeroOrden);
    }
    
    @Override
    public Orden guardarOrden(Orden orden) {
        return ordenRepository.save(orden);
    }
    
    @Override
    public void eliminarOrden(Long id) {
        ordenRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Orden> obtenerOrdenesPorUsuario(Usuario usuario) {
        return ordenRepository.findByUsuarioOrderByFechaCreacionDesc(usuario);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Orden> obtenerOrdenesPorUsuario(Usuario usuario, Pageable pageable) {
        return ordenRepository.findByUsuarioOrderByFechaCreacionDesc(usuario, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Orden> obtenerTodasLasOrdenes(Pageable pageable) {
        return ordenRepository.findAllByOrderByFechaCreacionDesc(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Orden> obtenerOrdenesPorEstado(Orden.EstadoOrden estado, Pageable pageable) {
        return ordenRepository.findByEstadoOrderByFechaCreacionDesc(estado, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Orden> buscarOrdenes(String numeroOrden, Orden.EstadoOrden estado, String emailCliente, Pageable pageable) {
        return ordenRepository.findOrdersWithFilters(numeroOrden, estado, emailCliente, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long contarOrdenes() {
        return ordenRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long contarOrdenesPorEstado(Orden.EstadoOrden estado) {
        return ordenRepository.countByEstado(estado);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal obtenerTotalVentas() {
        BigDecimal total = ordenRepository.getTotalVentas();
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal obtenerTotalVentasPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        BigDecimal total = ordenRepository.getTotalVentasPorPeriodo(fechaInicio, fechaFin);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long contarOrdenesPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ordenRepository.contarOrdenesPorPeriodo(fechaInicio, fechaFin);
    }
    
    @Override
    public Orden actualizarEstado(Long ordenId, Orden.EstadoOrden nuevoEstado) {
        Optional<Orden> ordenOpt = ordenRepository.findById(ordenId);
        if (ordenOpt.isPresent()) {
            Orden orden = ordenOpt.get();
            orden.setEstado(nuevoEstado);
            return ordenRepository.save(orden);
        }
        throw new RuntimeException("Orden no encontrada con ID: " + ordenId);
    }
    
    @Override
    public Orden procesarPago(Long ordenId, Pago.MetodoPago metodoPago, String referenciaPago) {
        Optional<Orden> ordenOpt = ordenRepository.findById(ordenId);
        if (ordenOpt.isPresent()) {
            Orden orden = ordenOpt.get();
            
            // Crear el registro de pago
            Pago pago = new Pago(orden, orden.getTotal(), metodoPago);
            if (referenciaPago != null) {
                pago.setReferenciaPago(referenciaPago);
            }
            pago.marcarComoPagado();
            pagoRepository.save(pago);
            
            // Actualizar estado de la orden
            orden.setEstado(Orden.EstadoOrden.PAGADO);
            return ordenRepository.save(orden);
        }
        throw new RuntimeException("Orden no encontrada con ID: " + ordenId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean puedeModificarOrden(Long ordenId) {
        Optional<Orden> ordenOpt = ordenRepository.findById(ordenId);
        if (ordenOpt.isPresent()) {
            Orden orden = ordenOpt.get();
            return orden.getEstado() == Orden.EstadoOrden.PENDIENTE;
        }
        return false;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean puedeReembolsarOrden(Long ordenId) {
        Optional<Orden> ordenOpt = ordenRepository.findById(ordenId);
        if (ordenOpt.isPresent()) {
            Orden orden = ordenOpt.get();
            return orden.isPagado() && orden.getEstado() != Orden.EstadoOrden.REEMBOLSADO;
        }
        return false;
    }
}