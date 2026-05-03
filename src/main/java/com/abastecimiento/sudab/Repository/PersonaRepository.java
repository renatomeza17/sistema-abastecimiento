package com.abastecimiento.sudab.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abastecimiento.sudab.Model.Persona;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    // Útil para verificar si un trabajador ya está registrado por su DNI
    Optional<Persona> findByNumDocumento(String numDocumento);
}