package com.abastecimiento.sudab.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abastecimiento.sudab.Model.inventario.Kardex;


@Repository
public interface KardexRepository extends JpaRepository<Kardex, Long>{

    Optional<Kardex> findByProductoIdProducto(Long idProducto);


}
