package com.abastecimiento.sudab.Service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.abastecimiento.sudab.DTO.request.OrdenRequestDTO;
import com.abastecimiento.sudab.DTO.response.OrdenResponseDTO;
import com.abastecimiento.sudab.Model.compra.OrdenCompra;
import com.abastecimiento.sudab.Model.requerimiento.Proforma;
import com.abastecimiento.sudab.Repository.OrdenCompraRepository;
import com.abastecimiento.sudab.Repository.ProformaRepository;

@ExtendWith(MockitoExtension.class)
public class OrdenServiceTest {
// 1. Mocks: Simulamos las dependencias que inyecta tu constructor @RequiredArgsConstructor
    @Mock
    private ProformaRepository proformaRepository;

    @Mock
    private OrdenCompraRepository ordenCompraRepository;

    @Mock
    private KardexService kardexService; 

    // 2. InjectMocks: Instancia real de tu servicio que usará los repositorios simulados de arriba
    @InjectMocks
    private OrdenService ordenService;

    private Proforma proformaMock;
    private OrdenRequestDTO requestDTO;

    

    @BeforeEach
    void setUp() {
        // Inicialización de objetos comunes para las pruebas
        proformaMock = new Proforma();
        proformaMock.setIdProforma(101L);
        proformaMock.setEstado("PENDIENTE");
        proformaMock.setProductos(new ArrayList<>()); // Evita NullPointerException en tu bucle for

        requestDTO = new OrdenRequestDTO();
        requestDTO.setIdProforma(101L);
        requestDTO.setFechaEntrega("2026-07-15");
        requestDTO.setLugarEntrega("Almacén Central UNI");
    }




    // ==========================================
    // PRUEBA 1: Método generarOrden(...)
    // ==========================================
    @Test
    void cuandoGenerarOrdenConProformaSeleccionada_EntoncesLanzaIllegalStateException() {
        // Arrange (Configurar simulación): Forzamos el estado que activa el error en tu 'if'
        proformaMock.setEstado("SELECCIONADA");
        when(proformaRepository.findById(101L)).thenReturn(Optional.of(proformaMock));

        // Act & Assert (Ejecución y Verificación)
        assertThrows(IllegalStateException.class, () -> {
            ordenService.generarOrden(requestDTO);
        });

        // Verificación de seguridad: Asegura que el repositorio jamás llamó a guardar data corrupta
        verify(ordenCompraRepository, never()).save(any(OrdenCompra.class));
    }





    // ==========================================
    // PRUEBA 2: Método autorizarYFirmarOrden(...)
    // ==========================================
    @Test
    void cuandoAutorizarYFirmarOrden_EntoncesMutaEstadoAAPROBADAYGeneraHash() {
        // Arrange (Configurar simulación)
        OrdenCompra ordenPendiente = new OrdenCompra();
        ordenPendiente.setIdOrden(55L);
        ordenPendiente.setCodigo("OC-2026-1234");
        ordenPendiente.setEstado("PENDIENTE");

        when(ordenCompraRepository.findById(55L)).thenReturn(Optional.of(ordenPendiente));
        
        // Al ejecutar save(), simulamos que retorna el mismo objeto mutado
        when(ordenCompraRepository.save(any(OrdenCompra.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act (Ejecutar el método real de tu servicio)
        OrdenResponseDTO resultado = ordenService.autorizarYFirmarOrden(55L, "director_admin");

        // Assert (Validar que las mutaciones internas de tu código se cumplan)
        assertNotNull(resultado);
        assertEquals("APROBADA", ordenPendiente.getEstado());
        assertEquals("director_admin", ordenPendiente.getAutorizadoPor());
        
        // Verifica que tu lógica criptográfica ligera haya concatenado el prefijo del sistema
        assertTrue(ordenPendiente.getFirmaDigitalHash().startsWith("SUDAB-SIG-"));
        
        // Comprueba que los cambios se persistieron una sola vez en el repositorio
        verify(ordenCompraRepository, times(1)).save(ordenPendiente);
    }


}
