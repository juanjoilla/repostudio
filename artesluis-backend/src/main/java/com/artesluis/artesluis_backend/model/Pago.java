package com.artesluis.artesluis_backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class Pago {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;
    
    @Column(name = "referencia_pago", unique = true)
    private String referenciaPago;
    
    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago")
    private MetodoPago metodoPago;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoPago estado;
    
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Información de la pasarela de pago
    @Column(name = "gateway_transaction_id")
    private String gatewayTransactionId;
    
    @Column(name = "gateway_response", length = 1000)
    private String gatewayResponse;
    
    @Column(name = "gateway_status")
    private String gatewayStatus;
    
    // Información adicional del pago
    @Column(name = "numero_tarjeta_ultimos_4")
    private String numeroTarjetaUltimos4;
    
    @Column(name = "tipo_tarjeta")
    private String tipoTarjeta;
    
    @Column(name = "nombre_tarjetahabiente")
    private String nombreTarjetahabiente;
    
    @Column(name = "notas")
    private String notas;
    
    // Información de transferencia bancaria
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banco_id")
    private Banco banco;
    
    @Column(name = "numero_cuenta_origen")
    private String numeroCuentaOrigen;
    
    @Column(name = "numero_referencia_transferencia")
    private String numeroReferenciaTransferencia;
    
    public enum MetodoPago {
        TARJETA_CREDITO,
        TARJETA_DEBITO,
        PAYPAL,
        TRANSFERENCIA_BANCARIA,
        EFECTIVO,
        MERCADO_PAGO,
        STRIPE,
        SERVICIO_DIGITAL,
        REEMBOLSO,
        OTRO
    }
    
    public enum EstadoPago {
        PENDIENTE,
        PROCESANDO,
        COMPLETADO,
        FALLIDO,
        CANCELADO,
        REEMBOLSADO,
        PARCIALMENTE_REEMBOLSADO
    }
    
    // Constructores
    public Pago() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.estado = EstadoPago.PENDIENTE;
    }
    
    public Pago(Orden orden, BigDecimal monto, MetodoPago metodoPago) {
        this();
        this.orden = orden;
        this.monto = monto;
        this.metodoPago = metodoPago;
    }
    
    // Métodos de utilidad
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (referenciaPago == null) {
            referenciaPago = generatePaymentReference();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    private String generatePaymentReference() {
        return "PAY-" + System.currentTimeMillis();
    }
    
    public void marcarComoPagado() {
        this.estado = EstadoPago.COMPLETADO;
        this.fechaPago = LocalDateTime.now();
    }
    
    public void marcarComoFallido(String razon) {
        this.estado = EstadoPago.FALLIDO;
        this.notas = razon;
    }
    
    public boolean isCompletado() {
        return estado == EstadoPago.COMPLETADO;
    }
    
    public boolean isPendiente() {
        return estado == EstadoPago.PENDIENTE;
    }
    
    // Métodos de compatibilidad para el CheckoutController
    public boolean isPagado() {
        return estado == EstadoPago.COMPLETADO;
    }
    
    public String getRespuestaGateway() {
        return gatewayResponse;
    }
    
    public void setRespuestaGateway(String respuesta) {
        this.gatewayResponse = respuesta;
    }
    
    public void setUltimosDigitosTarjeta(String digitos) {
        this.numeroTarjetaUltimos4 = digitos;
    }
    
    public String getUltimosDigitosTarjeta() {
        return numeroTarjetaUltimos4;
    }
    
    public void setNombreTitular(String nombre) {
        this.nombreTarjetahabiente = nombre;
    }
    
    public String getNombreTitular() {
        return nombreTarjetahabiente;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Orden getOrden() {
        return orden;
    }
    
    public void setOrden(Orden orden) {
        this.orden = orden;
    }
    
    public String getReferenciaPago() {
        return referenciaPago;
    }
    
    public void setReferenciaPago(String referenciaPago) {
        this.referenciaPago = referenciaPago;
    }
    
    public BigDecimal getMonto() {
        return monto;
    }
    
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
    
    public MetodoPago getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public EstadoPago getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }
    
    public LocalDateTime getFechaPago() {
        return fechaPago;
    }
    
    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    public String getGatewayTransactionId() {
        return gatewayTransactionId;
    }
    
    public void setGatewayTransactionId(String gatewayTransactionId) {
        this.gatewayTransactionId = gatewayTransactionId;
    }
    
    public String getGatewayResponse() {
        return gatewayResponse;
    }
    
    public void setGatewayResponse(String gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }
    
    public String getGatewayStatus() {
        return gatewayStatus;
    }
    
    public void setGatewayStatus(String gatewayStatus) {
        this.gatewayStatus = gatewayStatus;
    }
    
    public String getNumeroTarjetaUltimos4() {
        return numeroTarjetaUltimos4;
    }
    
    public void setNumeroTarjetaUltimos4(String numeroTarjetaUltimos4) {
        this.numeroTarjetaUltimos4 = numeroTarjetaUltimos4;
    }
    
    public String getTipoTarjeta() {
        return tipoTarjeta;
    }
    
    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }
    
    public String getNombreTarjetahabiente() {
        return nombreTarjetahabiente;
    }
    
    public void setNombreTarjetahabiente(String nombreTarjetahabiente) {
        this.nombreTarjetahabiente = nombreTarjetahabiente;
    }
    
    public String getNotas() {
        return notas;
    }
    
    public void setNotas(String notas) {
        this.notas = notas;
    }
    
    public Banco getBanco() {
        return banco;
    }
    
    public void setBanco(Banco banco) {
        this.banco = banco;
    }
    
    public String getNumeroCuentaOrigen() {
        return numeroCuentaOrigen;
    }
    
    public void setNumeroCuentaOrigen(String numeroCuentaOrigen) {
        this.numeroCuentaOrigen = numeroCuentaOrigen;
    }
    
    public String getNumeroReferenciaTransferencia() {
        return numeroReferenciaTransferencia;
    }
    
    public void setNumeroReferenciaTransferencia(String numeroReferenciaTransferencia) {
        this.numeroReferenciaTransferencia = numeroReferenciaTransferencia;
    }
}