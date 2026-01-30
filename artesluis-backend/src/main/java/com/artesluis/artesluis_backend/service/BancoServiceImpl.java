package com.artesluis.artesluis_backend.service;

import com.artesluis.artesluis_backend.model.Banco;
import com.artesluis.artesluis_backend.repository.BancoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BancoServiceImpl implements BancoService {
    
    @Autowired
    private BancoRepository bancoRepository;
    
    @Override
    public List<Banco> obtenerBancosActivos() {
        return bancoRepository.findByEstaActivoTrue();
    }
    
    @Override
    public Optional<Banco> obtenerBancoPorCodigo(String codigo) {
        return bancoRepository.findByCodigo(codigo);
    }
    
    @Override
    public Optional<Banco> obtenerBancoPorId(Long id) {
        return bancoRepository.findById(id);
    }
    
    @Override
    public List<Banco> obtenerBancosPorTipo(Banco.TipoBanco tipoBanco) {
        return bancoRepository.findByTipoBancoAndEstaActivoTrue(tipoBanco);
    }
}
