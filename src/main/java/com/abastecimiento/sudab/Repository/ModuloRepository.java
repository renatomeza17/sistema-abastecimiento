package com.abastecimiento.sudab.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abastecimiento.sudab.Model.Modulo;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Integer> {
    // Para buscar módulos por su descripción (ej: "INVENTARIO")
    Optional<Modulo> findByDescripcion(String descripcion);
}