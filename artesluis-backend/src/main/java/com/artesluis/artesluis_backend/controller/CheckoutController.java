package com.artesluis.artesluis_backend.controller;

import com.artesluis.artesluis_backend.model.*;
import com.artesluis.artesluis_backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {
    
    @Autowired
    private OrdenService ordenService;
    
    @Autowired
    private PagoService pagoService;
    
    @Autowired
    private CarritoService carritoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private BancoService bancoService;
    
    /**
     * Muestra la página de checkout con resumen de la orden
     */
    @GetMapping
    public String mostrarCheckout(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        
        if (usuarioLogueado == null) {
            redirectAttributes.addFlashAttribute("mensaje", "Debe iniciar sesión para continuar con la compra");
            redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
            return "redirect:/login";
        }
        
        // Usar la misma lógica de sessionId que CarritoController
        String sessionId = session.getId();
        Carrito carrito = carritoService.obtenerCarritoPorSession(sessionId);
        List<ItemCarrito> itemsCarrito = carrito.getItems();
        
        if (itemsCarrito.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "Su carrito está vacío");
            redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
            return "redirect:/carrito";
        }
        
        // Calcular totales
        BigDecimal subtotal = itemsCarrito.stream()
                .map(item -> item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        model.addAttribute("usuario", usuarioLogueado);
        model.addAttribute("itemsCarrito", itemsCarrito);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("total", subtotal); // Por ahora sin impuestos o descuentos
        model.addAttribute("metodosPago", Pago.MetodoPago.values());
        model.addAttribute("bancos", bancoService.obtenerBancosActivos());
        
        return "checkout";
    }
    
    /**
     * Procesa el checkout y crea la orden
     */
    @PostMapping("/procesar")
    public String procesarCheckout(
            @RequestParam("metodoPago") String metodoPago,
            @RequestParam Map<String, String> parametros,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // LOGGING DETALLADO PARA DEBUG
        System.out.println("\n========== PROCESAR CHECKOUT ==========");
        System.out.println("Método de pago recibido: " + metodoPago);
        System.out.println("Parámetros recibidos:");
        parametros.forEach((key, value) -> System.out.println("  " + key + " = " + value));
        System.out.println("======================================\n");
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        
        if (usuarioLogueado == null) {
            redirectAttributes.addFlashAttribute("mensaje", "Sesión expirada. Inicie sesión nuevamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/login";
        }
        
        try {
            // Usar la misma lógica de sessionId que CarritoController
            String sessionId = session.getId();
            Carrito carrito = carritoService.obtenerCarritoPorSession(sessionId);
            List<ItemCarrito> itemsCarrito = carrito.getItems();
            
            if (itemsCarrito.isEmpty()) {
                redirectAttributes.addFlashAttribute("mensaje", "Su carrito está vacío");
                redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
                return "redirect:/carrito";
            }
            
            // Crear la orden
            Orden orden = ordenService.crearOrden(usuarioLogueado, itemsCarrito);
            
            // Procesar el pago según el método seleccionado
            Pago pago = procesarPagoPorMetodo(orden, metodoPago, parametros);
            
            if (pago.getEstado() == Pago.EstadoPago.COMPLETADO) {
                // Limpiar el carrito después del pago exitoso usando sessionId
                carritoService.limpiarCarrito(sessionId);
                
                redirectAttributes.addFlashAttribute("mensaje", "¡Pago procesado exitosamente!");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
                redirectAttributes.addFlashAttribute("ordenId", orden.getId());
                redirectAttributes.addFlashAttribute("numeroOrden", orden.getNumeroOrden());
                
                return "redirect:/checkout/confirmacion/" + orden.getId();
            } else if (pago.getEstado() == Pago.EstadoPago.PENDIENTE) {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "Su pago está pendiente de verificación. Le notificaremos cuando sea procesado.");
                redirectAttributes.addFlashAttribute("tipoMensaje", "info");
                return "redirect:/checkout/confirmacion/" + orden.getId();
            } else {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "Error en el procesamiento del pago: " + pago.getRespuestaGateway());
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/checkout";
            }
            
        } catch (Exception e) {
            // Log del error completo para debugging
            System.err.println("\n========== ERROR EN CHECKOUT ==========");
            System.err.println("Tipo de error: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("Stack trace:");
            e.printStackTrace();
            System.err.println("======================================\n");
            
            redirectAttributes.addFlashAttribute("mensaje", 
                "Error al procesar la orden: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/checkout";
        }
    }
    
    /**
     * Muestra la página de confirmación de la orden
     */
    @GetMapping("/confirmacion/{ordenId}")
    public String mostrarConfirmacion(
            @PathVariable Long ordenId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("\n========== MOSTRAR CONFIRMACIÓN ==========");
        System.out.println("Orden ID: " + ordenId);
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        
        if (usuarioLogueado == null) {
            System.out.println("ERROR: Usuario no logueado");
            redirectAttributes.addFlashAttribute("mensaje", "Sesión expirada");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/login";
        }
        
        System.out.println("Usuario logueado: " + usuarioLogueado.getCorreo());
        
        try {
            Orden orden = ordenService.obtenerPorId(ordenId)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
            
            System.out.println("Orden encontrada: " + orden.getNumeroOrden());
            System.out.println("Usuario de la orden: " + orden.getUsuario().getId());
            System.out.println("Usuario logueado: " + usuarioLogueado.getId());
            
            // Verificar que la orden pertenece al usuario logueado
            if (!orden.getUsuario().getId().equals(usuarioLogueado.getId())) {
                System.out.println("ERROR: La orden no pertenece al usuario");
                redirectAttributes.addFlashAttribute("mensaje", "No tiene permisos para ver esta orden");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/";
            }
            
            List<Pago> pagos = pagoService.obtenerPagosPorOrden(orden);
            System.out.println("Pagos encontrados: " + pagos.size());
            
            model.addAttribute("orden", orden);
            model.addAttribute("pagos", pagos);
            model.addAttribute("usuario", usuarioLogueado);
            
            return "checkout-confirmacion";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", 
                "Error al cargar la confirmación: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/";
        }
    }
    
    /**
     * Procesa el pago según el método seleccionado
     */
    private Pago procesarPagoPorMetodo(Orden orden, String metodoPago, Map<String, String> parametros) {
        Pago.MetodoPago metodo = Pago.MetodoPago.valueOf(metodoPago);
        
        switch (metodo) {
            case TARJETA_CREDITO:
            case TARJETA_DEBITO:
                return pagoService.procesarPagoConTarjeta(orden,
                        parametros.get("numeroTarjeta"),
                        parametros.get("nombreTitular"),
                        parametros.get("mesVencimiento"),
                        parametros.get("anoVencimiento"),
                        parametros.get("cvv"));
                        
            case TRANSFERENCIA_BANCARIA:
                String bancoIdStr = parametros.get("bancoId");
                Long bancoId = bancoIdStr != null && !bancoIdStr.isEmpty() ? Long.parseLong(bancoIdStr) : null;
                return pagoService.procesarPagoTransferencia(orden,
                        bancoId,
                        parametros.get("referenciaBancaria"));
                        
            case SERVICIO_DIGITAL:
                return pagoService.procesarPagoDigital(orden,
                        parametros.get("servicioDigital"),
                        parametros.get("emailServicio"));
                        
            case EFECTIVO:
                return pagoService.procesarPago(orden, metodo, 
                        "PAGO-EFECTIVO-" + System.currentTimeMillis());
                        
            default:
                return pagoService.procesarPago(orden, metodo, null);
        }
    }
}