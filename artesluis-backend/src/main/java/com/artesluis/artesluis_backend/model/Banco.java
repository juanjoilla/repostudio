package com.artesluis.artesluis_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bancos")
public class Banco {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String codigo;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(name = "nombre_corto")
    private String nombreCorto;
    
    @Column(name = "logo_url")
    private String logoUrl;
    
    @Column(name = "esta_activo")
    private Boolean estaActivo = true;
    
    @Column(name = "tipo_banco")
    @Enumerated(EnumType.STRING)
    private TipoBanco tipoBanco;
    
    public enum TipoBanco {
        COMERCIAL,
        COOPERATIVO,
        DIGITAL,
        INTERNACIONAL
    }
    
    // Constructores
    public Banco() {
    }
    
    public Banco(String codigo, String nombre, String nombreCorto, TipoBanco tipoBanco) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.nombreCorto = nombreCorto;
        this.tipoBanco = tipoBanco;
        this.estaActivo = true;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getNombreCorto() {
        return nombreCorto;
    }
    
    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }
    
    public String getLogoUrl() {
        return logoUrl;
    }
    
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
    
    public Boolean getEstaActivo() {
        return estaActivo;
    }
    
    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }
    
    public TipoBanco getTipoBanco() {
        return tipoBanco;
    }
    
    public void setTipoBanco(TipoBanco tipoBanco) {
        this.tipoBanco = tipoBanco;
    }
}
