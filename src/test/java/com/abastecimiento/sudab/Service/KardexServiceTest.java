package com.abastecimiento.sudab.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.abastecimiento.sudab.DTO.request.KardexRequestDTO;
import com.abastecimiento.sudab.Model.Producto;
import com.abastecimiento.sudab.Model.inventario.Kardex;
import com.abastecimiento.sudab.Model.inventario.KardexMovimiento;
import com.abastecimiento.sudab.Repository.KardexMovimientoRepository;
import com.abastecimiento.sudab.Repository.KardexRepository;
import com.abastecimiento.sudab.Repository.ProductoRepository;

import jakarta.persistence.EntityNotFoundException;



@ExtendWith(MockitoExtension.class)
public class KardexServiceTest {

    // 1. Mocks: Simulamos las tres dependencias declaradas en tu constructor
    @Mock
    private KardexRepository kardexRepository;

    @Mock
    private KardexMovimientoRepository movimientoRepository;

    @Mock
    private ProductoRepository productoRepository;

    // 2. InjectMocks: Instancia real bajo prueba
    @InjectMocks
    private KardexService kardexService;

    private Producto productoMock;
    private KardexRequestDTO requestDTO;
    private Kardex kardexMock;

    @BeforeEach
    void setUp() {
        // Inicializar Producto del catálogo maestro
        productoMock = new Producto();
        productoMock.setIdProducto(5001L);
        productoMock.setCodigo("PROD-BOND");
        productoMock.setNombre("Papel Bond A4");

        // Inicializar DTO de petición para la HU11
        requestDTO = new KardexRequestDTO();
        requestDTO.setIdProducto(5001L);
        requestDTO.setStockMinimo(10);
        requestDTO.setUbicacionAlmacen("A-22-33");
        requestDTO.setCaracteristicas("Millar de 80gr");

        // Inicializar Cabecera de Kárdex común para la HU10
        kardexMock = new Kardex();
        kardexMock.setIdKardex(1L);
        kardexMock.setProducto(productoMock);
        kardexMock.setStockActual(15); // Empezamos con 15 unidades en el estante
        kardexMock.setStockMinimo(10);
    }



    // =========================================================================
    // PRUEBAS PARA HU11: registrarNuevoAsiento(KardexRequestDTO)
    // =========================================================================

    @Test
    void cuandoRegistrarNuevoAsientoExitoso_EntoncesNaceConStockCeroYGuarda() {
        // Arrange
        when(productoRepository.findById(5001L)).thenReturn(Optional.of(productoMock));
        when(kardexRepository.findByProductoIdProducto(5001L)).thenReturn(Optional.empty()); // No tiene kárdex previo
        when(kardexRepository.save(any(Kardex.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Kardex resultado = kardexService.registrarNuevoAsiento(requestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(productoMock, resultado.getProducto());
        assertEquals(0, resultado.getStockActual()); // Regla de negocio: Ficha técnica nace en 0
        assertEquals("A-22-33", resultado.getUbicacionAlmacen());
        verify(kardexRepository, times(1)).save(any(Kardex.class));
    }

    @Test
    void cuandoRegistrarAsientoDeProductoConKardexExistente_EntoncesLanzaIllegalStateException() {
        // Arrange: Simulamos que findByProductoIdProducto ya encuentra un Kárdex activo
        when(productoRepository.findById(5001L)).thenReturn(Optional.of(productoMock));
        when(kardexRepository.findByProductoIdProducto(5001L)).thenReturn(Optional.of(new Kardex()));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            kardexService.registrarNuevoAsiento(requestDTO);
        });

        // Garantizamos que no se guardaron duplicados corruptos
        verify(kardexRepository, never()).save(any(Kardex.class));
    }



    

    // =========================================================================
    // PRUEBAS PARA HU10: registrarMovimiento(idProducto, cantidad, tipo, ...)
    // =========================================================================

    @Test
    void cuandoRegistrarMovimientoEntrada_EntoncesIncrementaStockYGuardaHistorico() {
        // Arrange
        when(kardexRepository.findByProductoIdProducto(5001L)).thenReturn(Optional.of(kardexMock));
        when(kardexRepository.save(any(Kardex.class))).thenReturn(kardexMock);
        when(movimientoRepository.save(any(KardexMovimiento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act: Añadimos 10 unidades vía Entrada (Por ejemplo, una Recepción Conforme de OC)
        kardexService.registrarMovimiento(5001L, 10, "ENTRADA", "OC-2026-001", "Ingreso Conforme");

        // Assert
        assertEquals(25, kardexMock.getStockActual()); // 15 iniciales + 10 entrada = 25
        verify(kardexRepository, times(1)).save(kardexMock);
        verify(movimientoRepository, times(1)).save(any(KardexMovimiento.class));
    }

    @Test
    void cuandoRegistrarMovimientoSalidaExitosa_EntoncesDecrementaStock() {
        // Arrange
        when(kardexRepository.findByProductoIdProducto(5001L)).thenReturn(Optional.of(kardexMock));

        // 2. Simulamos el guardado de la cabecera: Retorna el mismo objeto Kárdex actualizado
        when(kardexRepository.save(any(Kardex.class))).thenReturn(kardexMock);
        
        // 3. Simulamos el guardado en el historial: Captura el movimiento de salida y lo devuelve
        when(movimientoRepository.save(any(KardexMovimiento.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act: Retiramos 5 unidades (Stock actual es 15, así que es totalmente válido)
        kardexService.registrarMovimiento(5001L, 5, "SALIDA", "PEC-2026-045", "Despacho a Oficina");

        // Assert
        assertEquals(10, kardexMock.getStockActual()); // 15 iniciales - 5 salida = 10
        verify(kardexRepository, times(1)).save(kardexMock);
        verify(movimientoRepository, times(1)).save(any(KardexMovimiento.class));
    }




    @Test
    void cuandoSalidaExcedeStockActual_EntoncesLanzaIllegalArgumentExceptionYNoModifica() {
        // Arrange: El stock actual es 15
        when(kardexRepository.findByProductoIdProducto(5001L)).thenReturn(Optional.of(kardexMock));

        // Act & Assert: Intentamos retirar 30 unidades (Inviable, provocaría stock negativo)
        assertThrows(IllegalArgumentException.class, () -> {
            kardexService.registrarMovimiento(5001L, 30, "SALIDA", "PEC-2026-099", "Intento fallido");
        });

        // Verificamos que el stock quedó intacto protegiendo la base de datos de Neon
        assertEquals(15, kardexMock.getStockActual());
        verify(kardexRepository, never()).save(any(Kardex.class));
        verify(movimientoRepository, never()).save(any(KardexMovimiento.class));
    }

    @Test
    void cuandoProductoNoTieneKardexAperturado_EntoncesLanzaEntityNotFoundException() {
        // Arrange: El producto existe en el mundo pero nadie corrió la HU11 antes
        when(kardexRepository.findByProductoIdProducto(5001L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            kardexService.registrarMovimiento(5001L, 10, "ENTRADA", "OC-2026-001", "Error directo");
        });

        verify(movimientoRepository, never()).save(any(KardexMovimiento.class));
    }

}
