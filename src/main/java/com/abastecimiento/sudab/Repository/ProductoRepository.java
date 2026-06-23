package com.abastecimiento.sudab.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.abastecimiento.sudab.Model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Busca productos que no tienen fila asociada en el kardex 
    @Query("SELECT p FROM Producto p WHERE NOT EXISTS (SELECT k FROM Kardex k WHERE k.producto.idProducto = p.idProducto)")
    List<Producto> findProductosSinKardex();

    // NUEVO: Para listar solo lo disponible para los Jefes de Dependencia
    List<Producto> findByActivoTrue();
}