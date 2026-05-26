package com.abastecimiento.sudab.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abastecimiento.sudab.Model.requerimiento.Proforma;

@Repository
public interface ProformaRepository extends JpaRepository<Proforma, Long> {
    Optional<Proforma> findByCodigo(String codigo);
    // Todas las proformas de un requerimiento específico
    List<Proforma> findByRequerimiento_IdRequerimiento(Long idRequerimiento);
    // Todas las proformas de un proveedor específico
    List<Proforma> findByProveedor_IdProveedor(Long idProveedor);
}