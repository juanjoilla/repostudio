package com.artesluis.artesluis_backend.controller;

import com.artesluis.artesluis_backend.model.*;
import com.artesluis.artesluis_backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de órdenes y ventas - Solo para administradores
 */
@RestController
@RequestMapping("/api/admin/ordenes")
public class AdminOrdenesController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private OrdenService ordenService;
    
    @Autowired
    private PagoService pagoService;
    
    /**
     * Listar todas las órdenes con filtros y paginación
     */
    @PostMapping("")
    public ResponseEntity<Map<String, Object>> listarOrdenes(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verificar credenciales de administrador
            String adminEmail = (String) request.get("adminEmail");
            String adminPassword = (String) request.get("adminPassword");
            
            if (!verificarAdministrador(adminEmail, adminPassword, response)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // Obtener parámetros de filtrado
            String numeroOrden = (String) request.get("numeroOrden");
            String estadoStr = (String) request.get("estado");
            String emailCliente = (String) request.get("emailCliente");
            int page = request.get("page") != null ? (Integer) request.get("page") : 0;
            int size = request.get("size") != null ? (Integer) request.get("size") : 20;
            
            Orden.EstadoOrden estado = null;
            if (estadoStr != null && !estadoStr.trim().isEmpty()) {
                try {
                    estado = Orden.EstadoOrden.valueOf(estadoStr);
                } catch (IllegalArgumentException e) {
                    // Estado inválido, se ignora
                }
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Orden> ordenesPage = ordenService.buscarOrdenes(numeroOrden, estado, emailCliente, pageable);
            
            // Convertir órdenes a formato de respuesta
            List<Map<String, Object>> ordenes = ordenesPage.getContent().stream()
                .map(this::ordenToMap)
                .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("ordenes", ordenes);
            response.put("currentPage", ordenesPage.getNumber());
            response.put("totalPages", ordenesPage.getTotalPages());
            response.put("totalItems", ordenesPage.getTotalElements());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener órdenes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Obtener detalle de una orden específica
     */
    @PostMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerOrden(
            @PathVariable Long id,
            @RequestBody Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String adminEmail = credentials.get("adminEmail");
            String adminPassword = credentials.get("adminPassword");
            
            if (!verificarAdministrador(adminEmail, adminPassword, response)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            Orden orden = ordenService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
            
            Map<String, Object> ordenDetalle = ordenToMapDetallado(orden);
            
            response.put("success", true);
            response.put("orden", ordenDetalle);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener la orden: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Actualizar el estado de una orden
     */
    @PostMapping("/{id}/estado")
    public ResponseEntity<Map<String, Object>> actualizarEstadoOrden(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String adminEmail = request.get("adminEmail");
            String adminPassword = request.get("adminPassword");
            String nuevoEstadoStr = request.get("nuevoEstado");
            
            if (!verificarAdministrador(adminEmail, adminPassword, response)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            if (nuevoEstadoStr == null || nuevoEstadoStr.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Estado nuevo es requerido");
                return ResponseEntity.badRequest().body(response);
            }
            
            Orden.EstadoOrden nuevoEstado = Orden.EstadoOrden.valueOf(nuevoEstadoStr);
            Orden ordenActualizada = ordenService.actualizarEstado(id, nuevoEstado);
            
            response.put("success", true);
            response.put("message", "Estado actualizado correctamente");
            response.put("orden", ordenToMap(ordenActualizada));
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Estado inválido");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar el estado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Obtener estadísticas de ventas
     */
    @PostMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String adminEmail = (String) request.get("adminEmail");
            String adminPassword = (String) request.get("adminPassword");
            
            if (!verificarAdministrador(adminEmail, adminPassword, response)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // Período (días hacia atrás desde hoy)
            int dias = request.get("dias") != null ? (Integer) request.get("dias") : 30;
            LocalDateTime fechaFin = LocalDateTime.now();
            LocalDateTime fechaInicio = fechaFin.minusDays(dias);
            
            // Estadísticas generales
            Long totalOrdenes = ordenService.contarOrdenes();
            Long ordenesPendientes = ordenService.contarOrdenesPorEstado(Orden.EstadoOrden.PENDIENTE);
            Long ordenesPagadas = ordenService.contarOrdenesPorEstado(Orden.EstadoOrden.PAGADO);
            Long ordenesCompletadas = ordenService.contarOrdenesPorEstado(Orden.EstadoOrden.COMPLETADO);
            Long ordenesCanceladas = ordenService.contarOrdenesPorEstado(Orden.EstadoOrden.CANCELADO);
            
            BigDecimal totalVentas = ordenService.obtenerTotalVentas();
            if (totalVentas == null) totalVentas = BigDecimal.ZERO;
            
            BigDecimal ventasPeriodo = ordenService.obtenerTotalVentasPorPeriodo(fechaInicio, fechaFin);
            if (ventasPeriodo == null) ventasPeriodo = BigDecimal.ZERO;
            
            Long ordenesPeriodo = ordenService.contarOrdenesPorPeriodo(fechaInicio, fechaFin);
            
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalOrdenes", totalOrdenes);
            estadisticas.put("ordenesPendientes", ordenesPendientes);
            estadisticas.put("ordenesPagadas", ordenesPagadas);
            estadisticas.put("ordenesCompletadas", ordenesCompletadas);
            estadisticas.put("ordenesCanceladas", ordenesCanceladas);
            estadisticas.put("totalVentas", totalVentas);
            estadisticas.put("ventasPeriodo", ventasPeriodo);
            estadisticas.put("ordenesPeriodo", ordenesPeriodo);
            estadisticas.put("diasPeriodo", dias);
            
            // Calcular ticket promedio
            if (ordenesCompletadas > 0) {
                BigDecimal ticketPromedio = totalVentas.divide(
                    BigDecimal.valueOf(ordenesCompletadas), 2, BigDecimal.ROUND_HALF_UP);
                estadisticas.put("ticketPromedio", ticketPromedio);
            } else {
                estadisticas.put("ticketPromedio", BigDecimal.ZERO);
            }
            
            response.put("success", true);
            response.put("estadisticas", estadisticas);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Obtener reporte de ventas por período
     */
    @PostMapping("/reportes")
    public ResponseEntity<Map<String, Object>> reporteVentas(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String adminEmail = (String) request.get("adminEmail");
            String adminPassword = (String) request.get("adminPassword");
            
            if (!verificarAdministrador(adminEmail, adminPassword, response)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            int dias = request.get("dias") != null ? (Integer) request.get("dias") : 30;
            LocalDateTime fechaFin = LocalDateTime.now();
            LocalDateTime fechaInicio = fechaFin.minusDays(dias);
            
            // Obtener órdenes del período
            Pageable pageable = PageRequest.of(0, 1000);
            Page<Orden> ordenesPage = ordenService.obtenerTodasLasOrdenes(pageable);
            
            List<Orden> ordenesPeriodo = ordenesPage.getContent().stream()
                .filter(o -> o.getFechaCreacion().isAfter(fechaInicio) && 
                            o.getFechaCreacion().isBefore(fechaFin))
                .collect(Collectors.toList());
            
            // Agrupar por estado
            Map<String, Long> ventasPorEstado = ordenesPeriodo.stream()
                .collect(Collectors.groupingBy(
                    o -> o.getEstado().toString(),
                    Collectors.counting()
                ));
            
            // Calcular total por estado
            Map<String, BigDecimal> totalPorEstado = ordenesPeriodo.stream()
                .collect(Collectors.groupingBy(
                    o -> o.getEstado().toString(),
                    Collectors.reducing(
                        BigDecimal.ZERO,
                        Orden::getTotal,
                        BigDecimal::add
                    )
                ));
            
            Map<String, Object> reporte = new HashMap<>();
            reporte.put("periodo", dias + " días");
            reporte.put("fechaInicio", fechaInicio);
            reporte.put("fechaFin", fechaFin);
            reporte.put("totalOrdenes", ordenesPeriodo.size());
            reporte.put("ventasPorEstado", ventasPorEstado);
            reporte.put("totalPorEstado", totalPorEstado);
            
            response.put("success", true);
            response.put("reporte", reporte);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al generar reporte: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Métodos auxiliares
    private boolean verificarAdministrador(String email, String password, Map<String, Object> response) {
        if (email == null || password == null) {
            response.put("success", false);
            response.put("message", "Credenciales de administrador requeridas");
            return false;
        }
        
        Usuario admin = usuarioService.obtenerPorCorreo(email);
        
        if (admin == null || !admin.getPassword().equals(password) || 
            !admin.getRol().getNombre().equals("ADMIN")) {
            response.put("success", false);
            response.put("message", "Acceso denegado - Se requieren privilegios de administrador");
            return false;
        }
        
        return true;
    }
    
    private Map<String, Object> ordenToMap(Orden orden) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", orden.getId());
        map.put("numeroOrden", orden.getNumeroOrden());
        map.put("total", orden.getTotal());
        map.put("subtotal", orden.getSubtotal());
        map.put("descuento", orden.getDescuento());
        map.put("estado", orden.getEstado().toString());
        map.put("fechaCreacion", orden.getFechaCreacion());
        map.put("fechaActualizacion", orden.getFechaActualizacion());
        map.put("nombreCliente", orden.getNombreCliente());
        map.put("emailCliente", orden.getEmailCliente());
        map.put("telefonoCliente", orden.getTelefonoCliente());
        return map;
    }
    
    private Map<String, Object> ordenToMapDetallado(Orden orden) {
        Map<String, Object> map = ordenToMap(orden);
        
        // Agregar detalles de la orden
        if (orden.getDetalles() != null) {
            List<Map<String, Object>> detalles = orden.getDetalles().stream()
                .map(detalle -> {
                    Map<String, Object> detalleMap = new HashMap<>();
                    detalleMap.put("id", detalle.getId());
                    detalleMap.put("nombrePlan", detalle.getNombrePlan());
                    detalleMap.put("descripcionPlan", detalle.getDescripcionPlan());
                    detalleMap.put("cantidad", detalle.getCantidad());
                    detalleMap.put("precioUnitario", detalle.getPrecioUnitario());
                    detalleMap.put("descuentoUnitario", detalle.getDescuentoUnitario());
                    detalleMap.put("subtotal", detalle.calcularSubtotal());
                    return detalleMap;
                })
                .collect(Collectors.toList());
            map.put("detalles", detalles);
        }
        
        // Agregar información de pagos
        if (orden.getPagos() != null) {
            List<Map<String, Object>> pagos = orden.getPagos().stream()
                .map(pago -> {
                    Map<String, Object> pagoMap = new HashMap<>();
                    pagoMap.put("id", pago.getId());
                    pagoMap.put("monto", pago.getMonto());
                    pagoMap.put("metodoPago", pago.getMetodoPago().toString());
                    pagoMap.put("estado", pago.getEstado().toString());
                    pagoMap.put("fechaCreacion", pago.getFechaCreacion());
                    pagoMap.put("referenciaPago", pago.getReferenciaPago());
                    return pagoMap;
                })
                .collect(Collectors.toList());
            map.put("pagos", pagos);
        }
        
        map.put("notas", orden.getNotas());
        
        return map;
    }
}
