package com.artesluis.artesluis_backend.service;

import com.artesluis.artesluis_backend.model.Carrito;
import com.artesluis.artesluis_backend.model.ItemCarrito;
import com.artesluis.artesluis_backend.model.Plan;
import com.artesluis.artesluis_backend.repository.CarritoRepository;
import com.artesluis.artesluis_backend.repository.ItemCarritoRepository;
import com.artesluis.artesluis_backend.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {
    
    @Autowired
    private CarritoRepository carritoRepository;
    
    @Autowired
    private ItemCarritoRepository itemCarritoRepository;
    
    @Autowired
    private PlanRepository planRepository;
    
    @Override
    public Carrito obtenerCarritoPorSession(String sessionId) {
        return carritoRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    Carrito nuevoCarrito = new Carrito(sessionId);
                    nuevoCarrito.setItems(new ArrayList<>());
                    return carritoRepository.save(nuevoCarrito);
                });
    }
    
    @Override
    public Carrito agregarPlanAlCarrito(String sessionId, Long planId, Integer cantidad) {
        Carrito carrito = obtenerCarritoPorSession(sessionId);
        Plan plan = planRepository.findByIdAndEstaActivoTrue(planId);
        
        if (plan == null) {
            throw new RuntimeException("Plan no encontrado o no disponible");
        }
        
        // Buscar si el item ya existe en el carrito
        Optional<ItemCarrito> itemExistente = itemCarritoRepository.findBySessionIdAndPlanId(sessionId, planId);
        
        if (itemExistente.isPresent()) {
            // Actualizar cantidad si ya existe
            ItemCarrito item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            itemCarritoRepository.save(item);
        } else {
            // Crear nuevo item
            ItemCarrito nuevoItem = new ItemCarrito(carrito, plan, cantidad);
            itemCarritoRepository.save(nuevoItem);
        }
        
        return carritoRepository.findBySessionId(sessionId).orElse(carrito);
    }
    
    @Override
    public Carrito actualizarCantidadItem(String sessionId, Long planId, Integer nuevaCantidad) {
        Optional<ItemCarrito> item = itemCarritoRepository.findBySessionIdAndPlanId(sessionId, planId);
        
        if (item.isPresent()) {
            if (nuevaCantidad <= 0) {
                itemCarritoRepository.delete(item.get());
            } else {
                item.get().setCantidad(nuevaCantidad);
                itemCarritoRepository.save(item.get());
            }
        }
        
        return obtenerCarritoPorSession(sessionId);
    }
    
    @Override
    public void removerPlanDelCarrito(String sessionId, Long planId) {
        Optional<ItemCarrito> item = itemCarritoRepository.findBySessionIdAndPlanId(sessionId, planId);
        if (item.isPresent()) {
            itemCarritoRepository.delete(item.get());
        }
    }
    
    @Override
    public void limpiarCarrito(String sessionId) {
        itemCarritoRepository.deleteByCarritoSessionId(sessionId);
        carritoRepository.deleteBySessionId(sessionId);
    }
    
    @Override
    public int contarItemsEnCarrito(String sessionId) {
        Carrito carrito = carritoRepository.findBySessionId(sessionId).orElse(null);
        return carrito != null ? carrito.contarItems() : 0;
    }
}