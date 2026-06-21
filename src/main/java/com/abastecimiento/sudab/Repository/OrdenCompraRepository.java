package com.abastecimiento.sudab.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abastecimiento.sudab.Model.compra.OrdenCompra;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
    
    Optional<OrdenCompra> findByCodigo(String codigo);

    Optional<OrdenCompra> findByProveedorIdProveedor(Long idProveedor);

     List<OrdenCompra> findByEstado(String estado);
}
