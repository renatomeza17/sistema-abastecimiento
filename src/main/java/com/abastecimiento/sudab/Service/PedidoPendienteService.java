package com.abastecimiento.sudab.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.abastecimiento.sudab.Model.compra.OrdenCompra;
import com.abastecimiento.sudab.Model.recepcion.PedidoPendiente;
import com.abastecimiento.sudab.Repository.OrdenCompraRepository;
import com.abastecimiento.sudab.Repository.PedidoPendienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoPendienteService {

    private final PedidoPendienteRepository pedidoPendienteRepository;
    private final OrdenCompraRepository ordenCompraRepository;

    // Registrar un pedido pendiente
    public PedidoPendiente registrar(Long idOrden, String motivo) {

        OrdenCompra orden = ordenCompraRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        PedidoPendiente pedido = new PedidoPendiente();
        pedido.setOrdenCompra(orden);
        pedido.setMotivo(motivo);
        pedido.setEstado("PENDIENTE");
        pedido.setFechaRegistro(LocalDateTime.now());

        return pedidoPendienteRepository.save(pedido);
    }

    // Listar todos los pedidos pendientes
    public List<PedidoPendiente> listar() {
        return pedidoPendienteRepository.findAll();
    }

    // Resolver un pedido pendiente
    public PedidoPendiente resolver(Long id) {

        PedidoPendiente pedido = pedidoPendienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido pendiente no encontrado"));

        pedido.setEstado("RESUELTO");
        pedido.setFechaResolucion(LocalDateTime.now());

        return pedidoPendienteRepository.save(pedido);
    }
}