package com.abastecimiento.sudab.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abastecimiento.sudab.Model.Usuario;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Para el Login Institucional (HU01)
    Optional<Usuario> findByUsername(String username);
}