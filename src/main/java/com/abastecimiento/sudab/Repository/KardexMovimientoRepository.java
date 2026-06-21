package com.abastecimiento.sudab.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abastecimiento.sudab.Model.inventario.KardexMovimiento;



@Repository
public interface KardexMovimientoRepository extends JpaRepository<KardexMovimiento, Long> {
    List<KardexMovimiento> findByKardexIdKardexOrderByFechaMovimientoDesc(Long IdKardex);

}
