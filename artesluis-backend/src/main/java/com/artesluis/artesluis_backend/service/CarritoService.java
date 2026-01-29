package com.artesluis.artesluis_backend.service;

import com.artesluis.artesluis_backend.model.Carrito;
import com.artesluis.artesluis_backend.model.Plan;

public interface CarritoService {
    
    Carrito obtenerCarritoPorSession(String sessionId);
    
    Carrito agregarPlanAlCarrito(String sessionId, Long planId, Integer cantidad);
    
    Carrito actualizarCantidadItem(String sessionId, Long planId, Integer nuevaCantidad);
    
    void removerPlanDelCarrito(String sessionId, Long planId);
    
    void limpiarCarrito(String sessionId);
    
    int contarItemsEnCarrito(String sessionId);
}