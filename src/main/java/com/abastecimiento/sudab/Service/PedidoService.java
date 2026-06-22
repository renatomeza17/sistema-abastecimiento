package com.abastecimiento.sudab.Service;

import com.abastecimiento.sudab.DTO.request.PedidoRequestDTO;
import com.abastecimiento.sudab.DTO.response.PedidoResponseDTO;
import com.abastecimiento.sudab.Model.Usuario;
import com.abastecimiento.sudab.Model.Producto;
import com.abastecimiento.sudab.Model.registro_pedido.Pedido;
import com.abastecimiento.sudab.Model.registro_pedido.PedidoDetalle;
import com.abastecimiento.sudab.Repository.PedidoRepository;
import com.abastecimiento.sudab.Repository.UsuarioRepository;
import com.abastecimiento.sudab.Repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public PedidoResponseDTO crearPedido(PedidoRequestDTO request) {
        // 1. EXTRAER EL USERNAME DIRECTAMENTE DE SPRING SECURITY (Inalterable desde el Front)
        String usernameAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Buscar al usuario por su username (usando el método de tu UsuarioRepository)
        Usuario usuario = usuarioRepository.findByUsername(usernameAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en el sistema: " + usernameAutenticado));

        // 3. Instanciar la cabecera del pedido
        Pedido pedido = new Pedido();
        pedido.setCodigo(generarCodigoCorrelativo());
        pedido.setDescripcion(request.getDescripcion());
        pedido.setEstado("PENDIENTE");
        pedido.setFechaCreacion(LocalDate.now());
        pedido.setUsuario(usuario);

        // 4. Mapear la lista de productos
        Pedido finalPedido = pedido; 
        List<PedidoDetalle> detalles = request.getDetalles().stream().map(dto -> {
            Producto producto = productoRepository.findById(dto.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getIdProducto()));

            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setPedido(finalPedido);
            detalle.setProducto(producto);
            detalle.setCantidad(dto.getCantidad());
            detalle.setObservacionEspecifica(dto.getObservacionEspecifica());
            return detalle;
        }).collect(Collectors.toList());

        pedido.setDetalles(detalles);

        // 5. Guardar en la BD
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        return convertirADto(pedidoGuardado);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPedidosPorUsuario() {
        // También limpiamos el listado para que solo traiga los pedidos del usuario logueado
        String usernameAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByUsername(usernameAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usernameAutenticado));

        List<Pedido> pedidos = pedidoRepository.findByUsuarioIdUsuarioOrderByFechaCreacionDesc(usuario.getIdUsuario());
        return pedidos.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    // --- El método convertirADto y generarCodigoCorrelativo se quedan exactamente igual ---
    private PedidoResponseDTO convertirADto(Pedido pedido) {
        List<PedidoResponseDTO.DetallePedidoResponseDTO> detallesDto = pedido.getDetalles().stream()
                .map(det -> PedidoResponseDTO.DetallePedidoResponseDTO.builder()
                        .idDetallePedido(det.getIdDetallePedido())
                        .idProducto(det.getProducto().getIdProducto())
                        .nombreProducto(det.getProducto().getNombre()) 
                        .unidadMedida(det.getProducto().getUnidadMedida()) 
                        .cantidad(det.getCantidad())
                        .observacionEspecifica(det.getObservacionEspecifica())
                        .build()
                ).collect(Collectors.toList());

        String nombreCompleto = "Sin Nombre";
        if (pedido.getUsuario() != null && pedido.getUsuario().getPersona() != null) {
            nombreCompleto = pedido.getUsuario().getPersona().getNombres() + " " + 
                             pedido.getUsuario().getPersona().getApellidoPaterno();
        } else if (pedido.getUsuario() != null) {
            nombreCompleto = pedido.getUsuario().getUsername();
        }

        return PedidoResponseDTO.builder()
                .idPedido(pedido.getIdPedido())
                .codigo(pedido.getCodigo())
                .descripcion(pedido.getDescripcion())
                .estado(pedido.getEstado())
                .fechaCreacion(pedido.getFechaCreacion())
                .nombreSolicitante(nombreCompleto)
                .detalles(detallesDto)
                .build();
    }

    private String generarCodigoCorrelativo() {
        int anioActual = LocalDate.now().getYear();
        String randomShort = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return "PED-" + anioActual + "-" + randomShort;
    }
}