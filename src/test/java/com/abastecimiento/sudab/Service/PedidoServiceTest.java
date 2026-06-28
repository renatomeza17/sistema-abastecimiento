package com.abastecimiento.sudab.Service;

import com.abastecimiento.sudab.DTO.request.PedidoRequestDTO;
import com.abastecimiento.sudab.DTO.response.PedidoResponseDTO;
import com.abastecimiento.sudab.Model.Producto;
import com.abastecimiento.sudab.Model.Usuario;
import com.abastecimiento.sudab.Model.registro_pedido.Pedido;
import com.abastecimiento.sudab.Repository.PedidoRepository;
import com.abastecimiento.sudab.Repository.ProductoRepository;
import com.abastecimiento.sudab.Repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private SecurityContext contextSecurity;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario usuarioMock;
    private Producto productoMock;
    private final String USERNAME_PRUEBA = "mitchell.sihuincha";

    @BeforeEach
    void setUp() {
        // Mockear el contexto de autenticación global de Spring Security
        SecurityContextHolder.setContext(contextSecurity);

        usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(10L);
        usuarioMock.setUsername(USERNAME_PRUEBA);
        usuarioMock.setEmail("mitchell@unmsm.edu.pe");

        productoMock = new Producto();
        productoMock.setIdProducto(101L);
        productoMock.setCodigo("PROD-101");
        productoMock.setNombre("Corrector Líquido");
        productoMock.setUnidadMedida("UNIDAD");
        productoMock.setActivo(true);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // --- Tests para CREAR PEDIDO ---

    @Test
    void crearPedido_DeberiaGuardarYRetornarDto_CuandoRequestSeaValido() {
        // Arrange
        when(contextSecurity.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USERNAME_PRUEBA);
        when(usuarioRepository.findByUsername(USERNAME_PRUEBA)).thenReturn(Optional.of(usuarioMock));
        when(productoRepository.findById(101L)).thenReturn(Optional.of(productoMock));

        // Preparar el Request DTO
        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setDescripcion("Abastecimiento mensual de oficina");
        
        PedidoRequestDTO.DetallePedidoRequestDTO detalleDto = new PedidoRequestDTO.DetallePedidoRequestDTO();
        detalleDto.setIdProducto(101L);
        detalleDto.setCantidad(5);
        detalleDto.setObservacionEspecifica("Color blanco premium");
        request.setDetalles(Collections.singletonList(detalleDto));

        // Stubbing para la persistencia del Repositorio
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedidoACrear = invocation.getArgument(0);
            pedidoACrear.setIdPedido(55L); // Simula el ID autogenerado por la Base de datos
            return pedidoACrear;
        });

        // Act
        PedidoResponseDTO resultado = pedidoService.crearPedido(request);

        // Assert
        assertNotNull(resultado);
        assertEquals(55L, resultado.getIdPedido());
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals("Abastecimiento mensual de oficina", resultado.getDescripcion());
        assertTrue(resultado.getCodigo().startsWith("PED-"));
        assertEquals(USERNAME_PRUEBA, resultado.getNombreSolicitante());
        assertEquals(1, resultado.getDetalles().size());
        assertEquals("Corrector Líquido", resultado.getDetalles().get(0).getNombreProducto());

        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void crearPedido_DeberiaLanzarExcepcion_CuandoUsuarioNoExisteEnContexto() {
        // Arrange
        when(contextSecurity.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USERNAME_PRUEBA);
        when(usuarioRepository.findByUsername(USERNAME_PRUEBA)).thenReturn(Optional.empty());

        PedidoRequestDTO request = new PedidoRequestDTO();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidoService.crearPedido(request);
        });

        assertTrue(exception.getMessage().contains("Usuario no encontrado en el sistema"));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void crearPedido_DeberiaLanzarExcepcion_CuandoProductoNoExisteEnCatalogo() {
        // Arrange
        when(contextSecurity.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USERNAME_PRUEBA);
        when(usuarioRepository.findByUsername(USERNAME_PRUEBA)).thenReturn(Optional.of(usuarioMock));
        when(productoRepository.findById(101L)).thenReturn(Optional.empty()); // El producto no existe

        PedidoRequestDTO request = new PedidoRequestDTO();
        PedidoRequestDTO.DetallePedidoRequestDTO detalleDto = new PedidoRequestDTO.DetallePedidoRequestDTO();
        detalleDto.setIdProducto(101L);
        request.setDetalles(Collections.singletonList(detalleDto));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidoService.crearPedido(request);
        });

        assertTrue(exception.getMessage().contains("Producto no encontrado con ID: 101"));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    // --- Tests para LISTAR PEDIDOS ---

    @Test
    void listarPedidosPorUsuario_DeberiaRetornarListaDePedidos_CuandoUsuarioTengaHistorial() {
        // Arrange
        when(contextSecurity.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USERNAME_PRUEBA);
        when(usuarioRepository.findByUsername(USERNAME_PRUEBA)).thenReturn(Optional.of(usuarioMock));

        Pedido pedidoHistorial = new Pedido();
        pedidoHistorial.setIdPedido(1L);
        pedidoHistorial.setCodigo("PED-2026-X1Y2Z");
        pedidoHistorial.setDescripcion("Pedido antiguo");
        pedidoHistorial.setEstado("FINALIZADO");
        pedidoHistorial.setUsuario(usuarioMock);
        pedidoHistorial.setDetalles(new ArrayList<>());

        when(pedidoRepository.findByUsuarioIdUsuarioOrderByFechaCreacionDesc(10L))
                .thenReturn(Collections.singletonList(pedidoHistorial));

        // Act
        List<PedidoResponseDTO> resultado = pedidoService.listarPedidosPorUsuario();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("PED-2026-X1Y2Z", resultado.get(0).getCodigo());
        assertEquals("FINALIZADO", resultado.get(0).getEstado());
        verify(pedidoRepository, times(1)).findByUsuarioIdUsuarioOrderByFechaCreacionDesc(10L);
    }
}