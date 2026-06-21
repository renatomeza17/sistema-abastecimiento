package com.abastecimiento.sudab.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abastecimiento.sudab.Model.recepcion.PedidoPendiente;

public interface PedidoPendienteRepository extends JpaRepository<PedidoPendiente, Long>{

    List<PedidoPendiente> findByEstado(String estado);

}