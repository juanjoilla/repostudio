package com.artesluis.artesluis_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad para almacenar notas y comentarios sobre órdenes
 * Útil para seguimiento administrativo
 */
@Entity
@Table(name = "notas_orden")
public class NotaOrden {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false, length = 2000)
    private String contenido;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNota tipo;
    
    public enum TipoNota {
        COMENTARIO,
        CAMBIO_ESTADO,
        SEGUIMIENTO,
        PROBLEMA,
        SOLUCION
    }
    
    // Constructores
    public NotaOrden() {
        this.fechaCreacion = LocalDateTime.now();
        this.tipo = TipoNota.COMENTARIO;
    }
    
    public NotaOrden(Orden orden, Usuario usuario, String contenido) {
        this();
        this.orden = orden;
        this.usuario = usuario;
        this.contenido = contenido;
    }
    
    public NotaOrden(Orden orden, Usuario usuario, String contenido, TipoNota tipo) {
        this(orden, usuario, contenido);
        this.tipo = tipo;
    }
    
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
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
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public String getContenido() {
        return contenido;
    }
    
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public TipoNota getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoNota tipo) {
        this.tipo = tipo;
    }
}
