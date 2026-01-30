package com.artesluis.artesluis_backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ordenes")
public class Orden {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_orden", unique = true, nullable = false)
    private String numeroOrden;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(name = "descuento", precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoOrden estado;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleOrden> detalles;
    
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pago> pagos;
    
    // Información de contacto
    @Column(name = "email_cliente")
    private String emailCliente;
    
    @Column(name = "telefono_cliente")
    private String telefonoCliente;
    
    @Column(name = "nombre_cliente")
    private String nombreCliente;
    
    @Column(name = "notas")
    private String notas;
    
    public enum EstadoOrden {
        PENDIENTE,
        PAGADO,
        EN_PROCESO,
        COMPLETADO,
        CANCELADO,
        REEMBOLSADO
    }
    
    // Constructores
    public Orden() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.estado = EstadoOrden.PENDIENTE;
        this.descuento = BigDecimal.ZERO;
    }
    
    public Orden(Usuario usuario, BigDecimal total) {
        this();
        this.usuario = usuario;
        this.total = total;
        this.subtotal = total;
        this.nombreCliente = usuario.getNombre();
        this.emailCliente = usuario.getCorreo();
    }
    
    // Métodos de utilidad
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (numeroOrden == null) {
            numeroOrden = generateOrderNumber();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }
    
    public void calcularTotal() {
        if (detalles != null) {
            subtotal = detalles.stream()
                .map(DetalleOrden::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            total = subtotal.subtract(descuento != null ? descuento : BigDecimal.ZERO);
        }
    }
    
    public boolean isPagado() {
        return estado == EstadoOrden.PAGADO || estado == EstadoOrden.EN_PROCESO || estado == EstadoOrden.COMPLETADO;
    }
    
    public BigDecimal getTotalPagado() {
        if (pagos == null) return BigDecimal.ZERO;
        return pagos.stream()
            .filter(pago -> pago.getEstado() == Pago.EstadoPago.COMPLETADO)
            .map(Pago::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNumeroOrden() {
        return numeroOrden;
    }
    
    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getDescuento() {
        return descuento;
    }
    
    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }
    
    public EstadoOrden getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
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
    
    public List<DetalleOrden> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetalleOrden> detalles) {
        this.detalles = detalles;
    }
    
    public List<Pago> getPagos() {
        return pagos;
    }
    
    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }
    
    public String getEmailCliente() {
        return emailCliente;
    }
    
    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }
    
    public String getTelefonoCliente() {
        return telefonoCliente;
    }
    
    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }
    
    public String getNombreCliente() {
        return nombreCliente;
    }
    
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    
    public String getNotas() {
        return notas;
    }
    
    public void setNotas(String notas) {
        this.notas = notas;
    }
}