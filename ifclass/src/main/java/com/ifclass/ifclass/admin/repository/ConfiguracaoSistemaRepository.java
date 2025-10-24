package com.ifclass.ifclass.admin.repository;

import com.ifclass.ifclass.admin.entity.ConfiguracaoSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracaoSistemaRepository extends JpaRepository<ConfiguracaoSistema, Long> {
    
    Optional<ConfiguracaoSistema> findByChave(String chave);
    
    List<ConfiguracaoSistema> findByCategoria(String categoria);
    
    List<ConfiguracaoSistema> findByEditavelTrue();
    
    @Query("SELECT DISTINCT c.categoria FROM ConfiguracaoSistema c ORDER BY c.categoria")
    List<String> findDistinctCategorias();
    
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE c.categoria = :categoria ORDER BY c.chave")
    List<ConfiguracaoSistema> findByCategoriaOrderByChave(@Param("categoria") String categoria);
    
    boolean existsByChave(String chave);
    
    @Query("SELECT c FROM ConfiguracaoSistema c WHERE c.chave LIKE :prefix% ORDER BY c.chave")
    List<ConfiguracaoSistema> findByChaveStartingWith(@Param("prefix") String prefix);
}
