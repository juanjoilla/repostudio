package com.artesluis.artesluis_backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "planes")
public class Plan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private BigDecimal precio;
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(name = "numero_revisiones")
    private Integer numeroRevisiones;
    
    @Column(name = "archivos_incluidos", length = 1000)
    private String archivosIncluidos;
    
    @Column(length = 2000)
    private String caracteristicas;
    
    @Column(name = "es_recomendado")
    private Boolean esRecomendado = false;
    
    @Column(name = "color_badge")
    private String colorBadge = "primary";
    
    @Column(name = "esta_activo")
    private Boolean estaActivo = true;
    
    // Constructores
    public Plan() {}
    
    public Plan(String nombre, BigDecimal precio, String descripcion, Integer numeroRevisiones,
                String archivosIncluidos, String caracteristicas, Boolean esRecomendado, String colorBadge) {
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.numeroRevisiones = numeroRevisiones;
        this.archivosIncluidos = archivosIncluidos;
        this.caracteristicas = caracteristicas;
        this.esRecomendado = esRecomendado;
        this.colorBadge = colorBadge;
        this.estaActivo = true;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public BigDecimal getPrecio() {
        return precio;
    }
    
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public Integer getNumeroRevisiones() {
        return numeroRevisiones;
    }
    
    public void setNumeroRevisiones(Integer numeroRevisiones) {
        this.numeroRevisiones = numeroRevisiones;
    }
    
    public String getArchivosIncluidos() {
        return archivosIncluidos;
    }
    
    public void setArchivosIncluidos(String archivosIncluidos) {
        this.archivosIncluidos = archivosIncluidos;
    }
    
    public String getCaracteristicas() {
        return caracteristicas;
    }
    
    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }
    
    public Boolean getEsRecomendado() {
        return esRecomendado;
    }
    
    public void setEsRecomendado(Boolean esRecomendado) {
        this.esRecomendado = esRecomendado;
    }
    
    public String getColorBadge() {
        return colorBadge;
    }
    
    public void setColorBadge(String colorBadge) {
        this.colorBadge = colorBadge;
    }
    
    public Boolean getEstaActivo() {
        return estaActivo;
    }
    
    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }
    
    public List<String> getCaracteristicasAsList() {
        if (caracteristicas == null || caracteristicas.isEmpty()) {
            return List.of();
        }
        return List.of(caracteristicas.split(";"));
    }
}