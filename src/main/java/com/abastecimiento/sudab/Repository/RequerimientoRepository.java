package com.abastecimiento.sudab.Repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.abastecimiento.sudab.Model.requerimiento.Requerimiento;

public interface RequerimientoRepository extends JpaRepository<Requerimiento, Long> {


        List<Requerimiento> findByEstado(String estado);

}
