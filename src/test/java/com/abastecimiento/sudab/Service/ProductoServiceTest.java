package com.abastecimiento.sudab.Service;

import com.abastecimiento.sudab.DTO.response.ProductoResponseDTO;
import com.abastecimiento.sudab.Model.Producto;
import com.abastecimiento.sudab.Repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto productoActivo1;
    private Producto productoActivo2;

    @BeforeEach
    void setUp() {
        productoActivo1 = new Producto();
        productoActivo1.setIdProducto(1L);
        productoActivo1.setCodigo("PROD-001");
        productoActivo1.setNombre("Papel Bond A4");
        productoActivo1.setDescripcion("Resma de 500 hojas");
        productoActivo1.setUnidadMedida("RESMA");
        productoActivo1.setStock(20);
        productoActivo1.setActivo(true);

        productoActivo2 = new Producto();
        productoActivo2.setIdProducto(2L);
        productoActivo2.setCodigo("PROD-002");
        productoActivo2.setNombre("Lapicero Azul Tinta Gel");
        productoActivo2.setDescripcion("Caja x 12 unidades");
        productoActivo2.setUnidadMedida("CAJA");
        productoActivo2.setStock(15);
        productoActivo2.setActivo(true);
    }

    @Test
    void listarProductosActivos_DeberiaRetornarListaDeDtos_CuandoExistanProductos() {
        // Arrange
        when(productoRepository.findByActivoTrue()).thenReturn(Arrays.asList(productoActivo1, productoActivo2));

        // Act
        List<ProductoResponseDTO> resultado = productoService.listarProductosActivos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        
        assertEquals("PROD-001", resultado.get(0).getCodigo());
        assertEquals("Papel Bond A4", resultado.get(0).getNombre());
        assertTrue(resultado.get(0).isActivo());

        assertEquals("PROD-002", resultado.get(1).getCodigo());
        assertEquals("CAJA", resultado.get(1).getUnidadMedida());
        
        verify(productoRepository, times(1)).findByActivoTrue();
    }

    @Test
    void listarProductosActivos_DeberiaRetornarListaVacia_CuandoNoHayanProductosActivos() {
        // Arrange
        when(productoRepository.findByActivoTrue()).thenReturn(Collections.emptyList());

        // Act
        List<ProductoResponseDTO> resultado = productoService.listarProductosActivos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(productoRepository, times(1)).findByActivoTrue();
    }
}