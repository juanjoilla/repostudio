package com.artesluis.artesluis_backend.controller.web;

import com.artesluis.artesluis_backend.model.*;
import com.artesluis.artesluis_backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminVentasController {
    
    @Autowired
    private OrdenService ordenService;
    
    @Autowired
    private PagoService pagoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    /**
     * Dashboard principal con estadísticas de ventas
     */
    @GetMapping("/ventas")
    public String mostrarDashboardVentas(
            @RequestParam(name = "periodo", defaultValue = "30") int diasPeriodo,
            HttpSession session, 
            Model model, 
            RedirectAttributes redirectAttributes) {
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        
        if (usuarioLogueado == null || !usuarioLogueado.isAdmin()) {
            redirectAttributes.addFlashAttribute("mensaje", "Acceso denegado. Solo administradores");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/login";
        }
        
        try {
            // Calcular fechas del período
            LocalDateTime fechaFin = LocalDateTime.now();
            LocalDateTime fechaInicio = fechaFin.minusDays(diasPeriodo);
            
            // Estadísticas generales
            Long totalOrdenes = ordenService.contarOrdenes();
            Long ordenesPendientes = ordenService.contarOrdenesPorEstado(Orden.EstadoOrden.PENDIENTE);
            Long ordenesPagadas = ordenService.contarOrdenesPorEstado(Orden.EstadoOrden.PAGADO);
            Long ordenesCompletadas = ordenService.contarOrdenesPorEstado(Orden.EstadoOrden.COMPLETADO);
            
            BigDecimal totalVentas = ordenService.obtenerTotalVentas();
            BigDecimal ventasPeriodo = ordenService.obtenerTotalVentasPorPeriodo(fechaInicio, fechaFin);
            Long ordenesPeriodo = ordenService.contarOrdenesPorPeriodo(fechaInicio, fechaFin);
            
            // Calcular ticket promedio
            BigDecimal ticketPromedio = BigDecimal.ZERO;
            if (totalOrdenes != null && totalOrdenes > 0 && totalVentas != null) {
                ticketPromedio = totalVentas.divide(new BigDecimal(totalOrdenes), 2, BigDecimal.ROUND_HALF_UP);
            }
            
            // Órdenes recientes
            Pageable pageable = PageRequest.of(0, 10);
            Page<Orden> ordenesRecientes = ordenService.obtenerTodasLasOrdenes(pageable);
            
            model.addAttribute("usuario", usuarioLogueado);
            model.addAttribute("totalOrdenes", totalOrdenes);
            model.addAttribute("ordenesPendientes", ordenesPendientes);
            model.addAttribute("ordenesPagadas", ordenesPagadas);
            model.addAttribute("ordenesCompletadas", ordenesCompletadas);
            model.addAttribute("totalVentas", totalVentas);
            model.addAttribute("ticketPromedio", ticketPromedio);
            model.addAttribute("ventasPeriodo", ventasPeriodo);
            model.addAttribute("ordenesPeriodo", ordenesPeriodo);
            model.addAttribute("diasPeriodo", diasPeriodo);
            model.addAttribute("ordenesRecientes", ordenesRecientes.getContent());
            model.addAttribute("fechaInicio", fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            model.addAttribute("fechaFin", fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            model.addAttribute("estados", Orden.EstadoOrden.values());
            
            return "admin/ventas-dashboard";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", 
                "Error al cargar las estadísticas: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/admin";
        }
    }
    
    /**
     * Lista todas las órdenes con filtros y paginación
     */
    @GetMapping("/ventas/ordenes")
    public String listarOrdenes(
            @RequestParam(name = "numeroOrden", required = false) String numeroOrden,
            @RequestParam(name = "estado", required = false) String estado,
            @RequestParam(name = "emailCliente", required = false) String emailCliente,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            HttpSession session, 
            Model model, 
            RedirectAttributes redirectAttributes) {
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        
        if (usuarioLogueado == null || !usuarioLogueado.isAdmin()) {
            redirectAttributes.addFlashAttribute("mensaje", "Acceso denegado. Solo administradores");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/login";
        }
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Orden.EstadoOrden estadoEnum = null;
            
            if (estado != null && !estado.trim().isEmpty()) {
                try {
                    estadoEnum = Orden.EstadoOrden.valueOf(estado.trim());
                } catch (IllegalArgumentException e) {
                    // Estado inválido, se ignora el filtro
                }
            }
            
            Page<Orden> ordenesPage = ordenService.buscarOrdenes(numeroOrden, estadoEnum, emailCliente, pageable);
            
            model.addAttribute("usuario", usuarioLogueado);
            model.addAttribute("ordenesPage", ordenesPage);
            model.addAttribute("numeroOrden", numeroOrden);
            model.addAttribute("estado", estado);
            model.addAttribute("emailCliente", emailCliente);
            model.addAttribute("estados", Orden.EstadoOrden.values());
            
            return "admin/ventas-ordenes";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", 
                "Error al cargar las órdenes: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/admin/ventas";
        }
    }
    
    /**
     * Muestra los detalles de una orden específica
     */
    @GetMapping("/ventas/orden/{ordenId}")
    public String verDetalleOrden(
            @PathVariable Long ordenId,
            HttpSession session, 
            Model model, 
            RedirectAttributes redirectAttributes) {
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        
        if (usuarioLogueado == null || !usuarioLogueado.isAdmin()) {
            redirectAttributes.addFlashAttribute("mensaje", "Acceso denegado. Solo administradores");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/login";
        }
        
        try {
            Orden orden = ordenService.obtenerPorId(ordenId)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
            
            List<Pago> pagos = pagoService.obtenerPagosPorOrden(orden);
            
            model.addAttribute("usuario", usuarioLogueado);
            model.addAttribute("orden", orden);
            model.addAttribute("pagos", pagos);
            model.addAttribute("estados", Orden.EstadoOrden.values());
            
            return "admin/ventas-detalle-orden";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", 
                "Error al cargar la orden: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/admin/ventas/ordenes";
        }
    }
    
    /**
     * Actualiza el estado de una orden
     */
    @PostMapping("/ventas/orden/{ordenId}/actualizar-estado")
    public String actualizarEstadoOrden(
            @PathVariable Long ordenId,
            @RequestParam("nuevoEstado") String nuevoEstado,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        
        if (usuarioLogueado == null || !usuarioLogueado.isAdmin()) {
            redirectAttributes.addFlashAttribute("mensaje", "Acceso denegado. Solo administradores");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/login";
        }
        
        try {
            Orden.EstadoOrden estadoEnum = Orden.EstadoOrden.valueOf(nuevoEstado);
            ordenService.actualizarEstado(ordenId, estadoEnum);
            
            redirectAttributes.addFlashAttribute("mensaje", "Estado de la orden actualizado correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensaje", "Estado inválido");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", 
                "Error al actualizar el estado: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        
        return "redirect:/admin/ventas/orden/" + ordenId;
    }
    
    /**
     * Procesa un reembolso para una orden
     */
    @PostMapping("/ventas/orden/{ordenId}/reembolsar")
    public String procesarReembolso(
            @PathVariable Long ordenId,
            @RequestParam("motivo") String motivo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        
        if (usuarioLogueado == null || !usuarioLogueado.isAdmin()) {
            redirectAttributes.addFlashAttribute("mensaje", "Acceso denegado. Solo administradores");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/login";
        }
        
        try {
            Orden orden = ordenService.obtenerPorId(ordenId)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
            
            if (!ordenService.puedeReembolsarOrden(ordenId)) {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "Esta orden no puede ser reembolsada");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/admin/ventas/orden/" + ordenId;
            }
            
            pagoService.procesarReembolso(orden, motivo);
            
            redirectAttributes.addFlashAttribute("mensaje", "Reembolso procesado correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", 
                "Error al procesar el reembolso: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        
        return "redirect:/admin/ventas/orden/" + ordenId;
    }
}