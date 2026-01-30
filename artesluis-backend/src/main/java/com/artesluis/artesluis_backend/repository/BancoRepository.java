package com.artesluis.artesluis_backend.repository;

import com.artesluis.artesluis_backend.model.Banco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BancoRepository extends JpaRepository<Banco, Long> {
    
    List<Banco> findByEstaActivoTrue();
    
    Optional<Banco> findByCodigo(String codigo);
    
    List<Banco> findByTipoBancoAndEstaActivoTrue(Banco.TipoBanco tipoBanco);
}
