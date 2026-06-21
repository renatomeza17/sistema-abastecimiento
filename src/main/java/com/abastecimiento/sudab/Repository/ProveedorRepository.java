package com.abastecimiento.sudab.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abastecimiento.sudab.Model.Proveedor;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Optional<Proveedor> findByRuc(String ruc);
    // NUEVO: Permite obtener el perfil de proveedor a partir de su ID de usuario vinculado
    Optional<Proveedor> findByUsuario_IdUsuario(Long idUsuario);
    // NUEVO: Permite obtener el perfil de proveedor a partir del username de su usuario vinculado
    Optional<Proveedor> findByUsuario_Username(String username);
}