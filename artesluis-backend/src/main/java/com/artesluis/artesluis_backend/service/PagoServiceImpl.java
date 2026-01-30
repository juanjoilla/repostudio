package com.artesluis.artesluis_backend.service;

import com.artesluis.artesluis_backend.model.Orden;
import com.artesluis.artesluis_backend.model.Pago;
import com.artesluis.artesluis_backend.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

@Service
@Transactional
public class PagoServiceImpl implements PagoService {
    
    @Autowired
    private PagoRepository pagoRepository;
    
    @Autowired
    private OrdenService ordenService;
    
    @Autowired
    private BancoService bancoService;
    
    private static final Pattern NUMERO_TARJETA_PATTERN = Pattern.compile("^[0-9]{13,19}$");
    private static final Pattern CVV_PATTERN = Pattern.compile("^[0-9]{3,4}$");
    
    @Override
    public Pago procesarPago(Orden orden, Pago.MetodoPago metodoPago, String referenciaPago) {
        // Verificar que la orden esté pendiente
        if (orden.getEstado() != Orden.EstadoOrden.PENDIENTE) {
            throw new RuntimeException("La orden ya ha sido procesada o cancelada");
        }
        
        // Crear el pago
        Pago pago = new Pago(orden, orden.getTotal(), metodoPago);
        
        if (referenciaPago != null && !referenciaPago.trim().isEmpty()) {
            pago.setReferenciaPago(referenciaPago.trim());
        }
        
        // Simular procesamiento del pago
        if (simularProcesamiento()) {
            pago.marcarComoPagado();
            orden.setEstado(Orden.EstadoOrden.PAGADO);
            ordenService.guardarOrden(orden);
        } else {
            pago.marcarComoFallido("Pago rechazado por la entidad bancaria");
        }
        
        return pagoRepository.save(pago);
    }
    
    @Override
    public Pago procesarPagoConTarjeta(Orden orden, String numeroTarjeta, String nombreTitular,
                                       String mesVencimiento, String anoVencimiento, String cvv) {
        // Validar datos de tarjeta
        if (!validarDatosTarjeta(numeroTarjeta, mesVencimiento, anoVencimiento, cvv)) {
            throw new RuntimeException("Datos de tarjeta inválidos");
        }
        
        if (nombreTitular == null || nombreTitular.trim().isEmpty()) {
            throw new RuntimeException("Nombre del titular es requerido");
        }
        
        // Crear el pago
        Pago pago = new Pago(orden, orden.getTotal(), Pago.MetodoPago.TARJETA_CREDITO);
        pago.setReferenciaPago(generarReferenciaPago());
        
        // Guardar datos de tarjeta (últimos 4 dígitos)
        String ultimosDigitos = numeroTarjeta.length() >= 4 ? 
                numeroTarjeta.substring(numeroTarjeta.length() - 4) : numeroTarjeta;
        pago.setNumeroTarjetaUltimos4(ultimosDigitos);
        pago.setNombreTarjetahabiente(nombreTitular.trim());
        
        // Simular procesamiento
        if (simularProcesamiento()) {
            pago.marcarComoPagado();
            pago.setGatewayResponse("Pago aprobado - Auth: " + generarCodigoAutorizacion());
            orden.setEstado(Orden.EstadoOrden.PAGADO);
            ordenService.guardarOrden(orden);
        } else {
            pago.marcarComoFallido("Tarjeta rechazada - Fondos insuficientes");
        }
        
        return pagoRepository.save(pago);
    }
    
    @Override
    public Pago procesarPagoTransferencia(Orden orden, Long bancoId, String referencia) {
        if (bancoId == null) {
            throw new RuntimeException("Banco es requerido para transferencias");
        }
        
        // Obtener el banco desde la base de datos
        com.artesluis.artesluis_backend.model.Banco banco = bancoService.obtenerBancoPorId(bancoId)
            .orElseThrow(() -> new RuntimeException("Banco no encontrado con ID: " + bancoId));
        
        Pago pago = new Pago(orden, orden.getTotal(), Pago.MetodoPago.TRANSFERENCIA_BANCARIA);
        pago.setReferenciaPago(referencia != null ? referencia.trim() : generarReferenciaPago());
        
        // Establecer la relación con el banco
        pago.setBanco(banco);
        pago.setNumeroReferenciaTransferencia(referencia != null ? referencia.trim() : null);
        pago.setNotas("Transferencia bancaria - Banco: " + banco.getNombre());
        
        // Las transferencias generalmente requieren verificación manual
        if (simularProcesamiento()) {
            pago.marcarComoPagado();
            pago.setGatewayResponse("Transferencia verificada - Banco: " + banco.getNombre() + " (" + banco.getCodigo() + ")");
            orden.setEstado(Orden.EstadoOrden.PAGADO);
            ordenService.guardarOrden(orden);
        } else {
            pago.setEstado(Pago.EstadoPago.PENDIENTE);
            pago.setGatewayResponse("Transferencia pendiente de verificación");
        }
        
        return pagoRepository.save(pago);
    }
    
    @Override
    public Pago procesarPagoDigital(Orden orden, String servicioDigital, String emailCuenta) {
        if (servicioDigital == null || servicioDigital.trim().isEmpty()) {
            throw new RuntimeException("Servicio digital es requerido");
        }
        
        if (emailCuenta == null || !emailCuenta.contains("@")) {
            throw new RuntimeException("Email de cuenta válido es requerido");
        }
        
        Pago pago = new Pago(orden, orden.getTotal(), Pago.MetodoPago.SERVICIO_DIGITAL);
        pago.setReferenciaPago(generarReferenciaPago());
        pago.setNotas("Servicio: " + servicioDigital.trim() + " - Email: " + emailCuenta.trim());
        
        // Simular procesamiento
        if (simularProcesamiento()) {
            pago.marcarComoPagado();
            pago.setGatewayResponse("Pago aprobado - " + servicioDigital + " ID: " + generarCodigoAutorizacion());
            orden.setEstado(Orden.EstadoOrden.PAGADO);
            ordenService.guardarOrden(orden);
        } else {
            pago.marcarComoFallido("Pago rechazado por " + servicioDigital);
        }
        
        return pagoRepository.save(pago);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean verificarEstadoPago(String referenciaPago) {
        return pagoRepository.findByReferenciaPago(referenciaPago)
                .map(pago -> pago.getEstado() == Pago.EstadoPago.COMPLETADO)
                .orElse(false);
    }
    
    @Override
    public Pago procesarReembolso(Orden orden, String motivo) {
        // Verificar que la orden esté pagada
        if (!orden.isPagado()) {
            throw new RuntimeException("Solo se pueden reembolsar órdenes pagadas");
        }
        
        if (orden.getEstado() == Orden.EstadoOrden.REEMBOLSADO) {
            throw new RuntimeException("La orden ya ha sido reembolsada");
        }
        
        // Crear el pago de reembolso
        Pago reembolso = new Pago(orden, orden.getTotal().negate(), Pago.MetodoPago.REEMBOLSO);
        reembolso.setReferenciaPago(generarReferenciaPago());
        reembolso.setGatewayResponse("Reembolso procesado - Motivo: " + 
                (motivo != null ? motivo : "No especificado"));
        reembolso.marcarComoPagado();
        
        // Actualizar estado de la orden
        orden.setEstado(Orden.EstadoOrden.REEMBOLSADO);
        ordenService.guardarOrden(orden);
        
        return pagoRepository.save(reembolso);
    }
    
    @Override
    public String generarReferenciaPago() {
        String prefijo = "PAG-";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String sufijo = String.format("%04d", new Random().nextInt(10000));
        return prefijo + timestamp + "-" + sufijo;
    }
    
    @Override
    public boolean validarDatosTarjeta(String numeroTarjeta, String mesVencimiento, 
                                       String anoVencimiento, String cvv) {
        // Validar número de tarjeta
        if (numeroTarjeta == null || !NUMERO_TARJETA_PATTERN.matcher(numeroTarjeta.replaceAll("\\s+", "")).matches()) {
            return false;
        }
        
        // Validar CVV
        if (cvv == null || !CVV_PATTERN.matcher(cvv).matches()) {
            return false;
        }
        
        // Validar fecha de vencimiento
        try {
            int mes = Integer.parseInt(mesVencimiento);
            int ano = Integer.parseInt(anoVencimiento);
            
            if (mes < 1 || mes > 12) {
                return false;
            }
            
            // Verificar que la tarjeta no esté vencida
            int anoActual = Year.now().getValue();
            if (ano < anoActual || (ano == anoActual && mes < LocalDateTime.now().getMonthValue())) {
                return false;
            }
            
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Pago obtenerPagoPorId(Long pagoId) {
        return pagoRepository.findById(pagoId).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Pago> obtenerPagosPorOrden(Orden orden) {
        return pagoRepository.findByOrdenOrderByFechaCreacionDesc(orden);
    }
    
    /**
     * Simula el procesamiento del pago con una tasa de éxito del 85%
     */
    private boolean simularProcesamiento() {
        // Simular delay de procesamiento
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 85% de probabilidad de éxito
        return new Random().nextDouble() < 0.85;
    }
    
    /**
     * Genera un código de autorización simulado
     */
    private String generarCodigoAutorizacion() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}