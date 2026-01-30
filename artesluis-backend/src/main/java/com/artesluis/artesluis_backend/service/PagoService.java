package com.artesluis.artesluis_backend.service;

import com.artesluis.artesluis_backend.model.Orden;
import com.artesluis.artesluis_backend.model.Pago;

public interface PagoService {
    
    /**
     * Procesa un pago para una orden específica
     */
    Pago procesarPago(Orden orden, Pago.MetodoPago metodoPago, String referenciaPago);
    
    /**
     * Simula un pago con tarjeta de crédito/débito
     */
    Pago procesarPagoConTarjeta(Orden orden, String numeroTarjeta, String nombreTitular,
                                String mesVencimiento, String anoVencimiento, String cvv);
    
    /**
     * Simula un pago con transferencia bancaria
     */
    Pago procesarPagoTransferencia(Orden orden, Long bancoId, String referencia);
    
    /**
     * Simula un pago con servicios digitales (PayPal, etc.)
     */
    Pago procesarPagoDigital(Orden orden, String servicioDigital, String emailCuenta);
    
    /**
     * Verifica el estado de un pago
     */
    boolean verificarEstadoPago(String referenciaPago);
    
    /**
     * Procesa un reembolso
     */
    Pago procesarReembolso(Orden orden, String motivo);
    
    /**
     * Genera una referencia única para el pago
     */
    String generarReferenciaPago();
    
    /**
     * Valida los datos de una tarjeta de crédito
     */
    boolean validarDatosTarjeta(String numeroTarjeta, String mesVencimiento, 
                               String anoVencimiento, String cvv);
    
    /**
     * Obtiene un pago por su ID
     */
    Pago obtenerPagoPorId(Long pagoId);
    
    /**
     * Obtiene pagos por orden
     */
    java.util.List<Pago> obtenerPagosPorOrden(Orden orden);
}