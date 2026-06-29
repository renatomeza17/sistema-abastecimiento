package com.abastecimiento.sudab.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.abastecimiento.sudab.Model.compra.OrdenCompra;
import com.abastecimiento.sudab.Model.recepcion.PedidoPendiente;
import com.abastecimiento.sudab.Repository.OrdenCompraRepository;
import com.abastecimiento.sudab.Repository.PedidoPendienteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PedidoPendienteServiceTest {

    @Mock
    private PedidoPendienteRepository pedidoPendienteRepository;

    @Mock
    private OrdenCompraRepository ordenCompraRepository;

    @InjectMocks
    private PedidoPendienteService pedidoPendienteService;

    private OrdenCompra ordenCompra;
    private PedidoPendiente pedidoPendiente;

    @BeforeEach
    void setUp() {
        ordenCompra = new OrdenCompra();
        ordenCompra.setIdOrden(1L);
        ordenCompra.setCodigo("OC-2026-001");
        ordenCompra.setEstado("ENVIADA");

        pedidoPendiente = new PedidoPendiente();
        pedidoPendiente.setOrdenCompra(ordenCompra);
        pedidoPendiente.setMotivo("Producto dañado");
        pedidoPendiente.setEstado("PENDIENTE");
    }

    @Test
    void debeRegistrarPedidoPendienteCuandoOrdenExiste() {
        when(ordenCompraRepository.findById(1L)).thenReturn(Optional.of(ordenCompra));
        when(pedidoPendienteRepository.save(any(PedidoPendiente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PedidoPendiente resultado = pedidoPendienteService.registrar(1L, "Producto dañado");

        assertNotNull(resultado);
        assertEquals(ordenCompra, resultado.getOrdenCompra());
        assertEquals("Producto dañado", resultado.getMotivo());
        assertEquals("PENDIENTE", resultado.getEstado());
        assertNotNull(resultado.getFechaRegistro());

        verify(ordenCompraRepository).findById(1L);
        verify(pedidoPendienteRepository).save(any(PedidoPendiente.class));
    }

    @Test
    void noDebeRegistrarPedidoPendienteSiOrdenNoExiste() {
        when(ordenCompraRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> pedidoPendienteService.registrar(99L, "Producto faltante")
        );

        assertEquals("Orden no encontrada", exception.getMessage());

        verify(ordenCompraRepository).findById(99L);
        verify(pedidoPendienteRepository, never()).save(any(PedidoPendiente.class));
    }

    @Test
    void debeListarPedidosPendientes() {
        when(pedidoPendienteRepository.findAll())
                .thenReturn(List.of(pedidoPendiente));

        List<PedidoPendiente> resultado = pedidoPendienteService.listar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Producto dañado", resultado.get(0).getMotivo());

        verify(pedidoPendienteRepository).findAll();
    }

    @Test
    void debeResolverPedidoPendienteCuandoExiste() {
        when(pedidoPendienteRepository.findById(1L)).thenReturn(Optional.of(pedidoPendiente));
        when(pedidoPendienteRepository.save(any(PedidoPendiente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PedidoPendiente resultado = pedidoPendienteService.resolver(1L);

        assertNotNull(resultado);
        assertEquals("RESUELTO", resultado.getEstado());
        assertNotNull(resultado.getFechaResolucion());

        verify(pedidoPendienteRepository).findById(1L);
        verify(pedidoPendienteRepository).save(pedidoPendiente);
    }

    @Test
    void noDebeResolverPedidoPendienteSiNoExiste() {
        when(pedidoPendienteRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> pedidoPendienteService.resolver(99L)
        );

        assertEquals("Pedido pendiente no encontrado", exception.getMessage());

        verify(pedidoPendienteRepository).findById(99L);
        verify(pedidoPendienteRepository, never()).save(any(PedidoPendiente.class));
    }

    @Test
    void noDebeResolverPedidoSiYaEstaResuelto() {
        // Arrange: Creamos un pedido que ya está RESUELTO
        PedidoPendiente pedidoResuelto = new PedidoPendiente();
        pedidoResuelto.setEstado("RESUELTO");
        when(pedidoPendienteRepository.findById(1L)).thenReturn(Optional.of(pedidoResuelto));

        // Act & Assert: El sistema debe impedir la acción
        assertThrows(IllegalStateException.class, () -> {
            pedidoPendienteService.resolver(1L);
        });

        // Verificamos que el save() NUNCA se ejecutó
        verify(pedidoPendienteRepository, never()).save(any(PedidoPendiente.class));
    }
}