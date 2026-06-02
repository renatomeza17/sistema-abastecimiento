package com.abastecimiento.sudab.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abastecimiento.sudab.Model.requerimiento.Requerimiento;

@Repository
public interface RequerimientoRepository extends JpaRepository<Requerimiento, Long> {
    Optional<Requerimiento> findByCodigo(String codigo);
    List<Requerimiento> findByEstado(String estado);
}