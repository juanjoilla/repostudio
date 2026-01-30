package com.artesluis.artesluis_backend.service;

import com.artesluis.artesluis_backend.model.Banco;
import java.util.List;
import java.util.Optional;

public interface BancoService {
    
    List<Banco> obtenerBancosActivos();
    
    Optional<Banco> obtenerBancoPorCodigo(String codigo);
    
    Optional<Banco> obtenerBancoPorId(Long id);
    
    List<Banco> obtenerBancosPorTipo(Banco.TipoBanco tipoBanco);
}
