package com.abastecimiento.sudab.Repository;

import com.abastecimiento.sudab.Model.registro_pedido.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    // Busca el historial de pedidos del jefe de dependencia, ordenados por el más reciente
    List<Pedido> findByUsuarioIdUsuarioOrderByFechaCreacionDesc(Long idUsuario);
}