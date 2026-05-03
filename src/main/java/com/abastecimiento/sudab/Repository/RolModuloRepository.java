package com.abastecimiento.sudab.Repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abastecimiento.sudab.Model.Modulo;
import com.abastecimiento.sudab.Model.Rol;
import com.abastecimiento.sudab.Model.RolModulo;






@Repository
public interface RolModuloRepository extends JpaRepository<RolModulo, Integer> {
    // Busca los permisos de un rol específico para un módulo específico
    List<RolModulo> findByRolAndModulo(Rol rol, Modulo modulo);
    List<RolModulo> findByRol(Rol rol);
}