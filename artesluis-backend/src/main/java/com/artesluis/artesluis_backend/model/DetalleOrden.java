package com.artesluis.artesluis_backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalles_orden")
public class DetalleOrden {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @Column(name = "descuento_unitario", precision = 10, scale = 2)
    private BigDecimal descuentoUnitario = BigDecimal.ZERO;
    
    // Información del plan al momento de la compra (para historial)
    @Column(name = "nombre_plan")
    private String nombrePlan;
    
    @Column(name = "descripcion_plan", length = 1000)
    private String descripcionPlan;
    
    // Constructores
    public DetalleOrden() {
        this.descuentoUnitario = BigDecimal.ZERO;
    }
    
    public DetalleOrden(Orden orden, Plan plan, Integer cantidad, BigDecimal precioUnitario) {
        this();
        this.orden = orden;
        this.plan = plan;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.nombrePlan = plan.getNombre();
        this.descripcionPlan = plan.getDescripcion();
    }
    
    // Métodos de utilidad
    public BigDecimal calcularSubtotal() {
        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        BigDecimal descuentoTotal = descuentoUnitario.multiply(BigDecimal.valueOf(cantidad));
        return subtotal.subtract(descuentoTotal);
    }
    
    public BigDecimal calcularDescuentoTotal() {
        return descuentoUnitario.multiply(BigDecimal.valueOf(cantidad));
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
    
    public Plan getPlan() {
        return plan;
    }
    
    public void setPlan(Plan plan) {
        this.plan = plan;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public BigDecimal getDescuentoUnitario() {
        return descuentoUnitario;
    }
    
    public void setDescuentoUnitario(BigDecimal descuentoUnitario) {
        this.descuentoUnitario = descuentoUnitario;
    }
    
    public String getNombrePlan() {
        return nombrePlan;
    }
    
    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
    }
    
    public String getDescripcionPlan() {
        return descripcionPlan;
    }
    
    public void setDescripcionPlan(String descripcionPlan) {
        this.descripcionPlan = descripcionPlan;
    }
}