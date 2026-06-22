package com.abastecimiento.sudab.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication; // Añadido
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abastecimiento.sudab.DTO.request.ProformaRequestDTO;
import com.abastecimiento.sudab.DTO.response.ProductoResponseDTO;
import com.abastecimiento.sudab.DTO.response.ProformaResponseDTO;
import com.abastecimiento.sudab.DTO.response.ProveedorResponseDTO;
import com.abastecimiento.sudab.Model.Producto;
import com.abastecimiento.sudab.Model.Proveedor;
import com.abastecimiento.sudab.Model.requerimiento.Proforma;
import com.abastecimiento.sudab.Model.requerimiento.ProformaDetalle;
import com.abastecimiento.sudab.Model.requerimiento.Requerimiento;
import com.abastecimiento.sudab.Repository.ProductoRepository;
import com.abastecimiento.sudab.Repository.ProformaRepository;
import com.abastecimiento.sudab.Repository.ProveedorRepository;
import com.abastecimiento.sudab.Repository.RequerimientoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProformaService {

    private final ProformaRepository proformaRepository;
    private final RequerimientoRepository requerimientoRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;

    // ── Crear proforma ─────────────────────────────────────────────────────────
    @Transactional
    public ProformaResponseDTO crear(ProformaRequestDTO dto) {

        // 1. Obtener el username del usuario logueado desde Spring Security
        String usernameAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Buscar el proveedor usando el username a través de la relación de tablas
        Proveedor proveedor = proveedorRepository.findByUsuario_Username(usernameAutenticado)
                .orElseThrow(() -> new RuntimeException("El usuario logueado '" + usernameAutenticado + "' no tiene un perfil de proveedor asignado."));

        // 3. Buscar el requerimiento
        Requerimiento req = requerimientoRepository.findById(dto.getIdRequerimiento())
                .orElseThrow(() -> new RuntimeException("Requerimiento no encontrado."));

        // 4. Generar código utilizando el proveedor obtenido de la sesión: PROF-{nombreProveedor}-{seq}
        long total = proformaRepository.count();
        String razonSocialLimpia = proveedor.getRazonSocial().toUpperCase().replaceAll("\\s+", "-");
        String codigoProv = razonSocialLimpia.substring(0, Math.min(8, razonSocialLimpia.length()));
        String codigo = String.format("PROF-%s-%04d", codigoProv, total + 1);

        // 5. Construir Proforma
        Proforma proforma = new Proforma();
        proforma.setCodigo(codigo);
        proforma.setFechaRecepcion(dto.getFechaRecepcion());
        proforma.setEstado("RECIBIDA");
        proforma.setRequerimiento(req);
        proforma.setProveedor(proveedor); 

        // 6. Construir detalles y calcular precio total
        List<ProformaDetalle> detalles = dto.getProductos().stream().map(p -> {
            Producto producto = productoRepository.findById(p.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + p.getIdProducto()));

            ProformaDetalle detalle = new ProformaDetalle();
            detalle.setProforma(proforma);
            detalle.setProducto(producto);
            detalle.setCantidad(p.getCantidad());
            detalle.setPrecioUnitario(p.getPrecioUnitario());
            return detalle;
        }).collect(Collectors.toList());

        // 7. Calcular precio total sumando subtotales
        double precioTotal = detalles.stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum();

        proforma.setPrecioTotal(precioTotal);
        proforma.setProductos(detalles);

        proformaRepository.save(proforma);
        return toResponse(proforma);
    }

    // ── Listar por requerimiento ───────────────────────────────────────────────
    public List<ProformaResponseDTO> listarPorRequerimiento(Long idRequerimiento) {
        return proformaRepository.findByRequerimiento_IdRequerimiento(idRequerimiento)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Listar por proveedor ───────────────────────────────────────────────────
    public List<ProformaResponseDTO> listarPorProveedor(Long idProveedor) {
        return proformaRepository.findByProveedor_IdProveedor(idProveedor)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Listar proformas del proveedor logueado ────────────────────────────────
    public List<ProformaResponseDTO> listarMisProformas() {
        // 1. Obtener el usuario autenticado desde el token JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usernameLogueado = auth.getName();

        // 2. Buscar al proveedor asociado a ese username en la base de datos
        Proveedor proveedor = proveedorRepository.findByUsuario_Username(usernameLogueado)
                .orElseThrow(() -> new RuntimeException("No se encontró un proveedor asociado a este usuario"));

        // 3. Buscar las proformas de ese proveedor y convertirlas a DTO
        List<Proforma> misProformas = proformaRepository.findByProveedor_IdProveedor(proveedor.getIdProveedor());
        
        return misProformas.stream()
                .map(this::toResponse) // CORREGIDO: toResponse en lugar de toResponseDTO
                .collect(Collectors.toList());
    }

    // ── Obtener por ID ─────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public ProformaResponseDTO obtenerPorIdService(Long idProforma) {
        Proforma proforma = proformaRepository.findById(idProforma)
            .orElseThrow(() -> new RuntimeException("Proforma no encontrada con el ID: " + idProforma));

        return this.toResponse(proforma);
    }

    // ── Elegir proforma (Jefe elige la mejor) ──────────────────────────────────
    @Transactional
    public ProformaResponseDTO elegir(Long idProforma) {
        Proforma elegida = proformaRepository.findById(idProforma)
                .orElseThrow(() -> new RuntimeException("Proforma no encontrada."));

        // Rechaza todas las demás del mismo requerimiento
        proformaRepository
        .findByRequerimiento_IdRequerimiento(elegida.getRequerimiento().getIdRequerimiento())
        .forEach(p -> p.setEstado(p.getIdProforma().equals(idProforma) ? "ELEGIDA" : "RECHAZADA"));

        // Cambia estado del requerimiento a EN_PROCESO
        Requerimiento req = elegida.getRequerimiento();
        req.setEstado("EN_PROCESO");
        requerimientoRepository.save(req);

        return toResponse(elegida);
    }

    // ── Listar Proformas Elegidas ──────────────────────────────────────────────
    public List<ProformaResponseDTO> listarProformasElegidas() {
        // CORREGIDO: Buscar por "ELEGIDA" (las ganadoras) en lugar de "PENDIENTE"
        return proformaRepository.findByEstado("ELEGIDA") 
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Mapper ─────────────────────────────────────────────────────────────────
    private ProformaResponseDTO toResponse(Proforma p) {

        ProveedorResponseDTO proveedorDto = ProveedorResponseDTO.builder()
                .idProveedor(p.getProveedor().getIdProveedor())
                .ruc(p.getProveedor().getRuc())
                .razonSocial(p.getProveedor().getRazonSocial())
                .direccion(p.getProveedor().getDireccion())
                .telefono(p.getProveedor().getTelefono())
                .email(p.getProveedor().getEmail())
                .contacto(p.getProveedor().getContacto())
                .build();

        List<ProformaResponseDTO.DetalleProformaResponseDTO> detalles = p.getProductos()
                .stream().map(d -> {
                        ProductoResponseDTO productoDto = ProductoResponseDTO.builder()
                            .idProducto(d.getProducto().getIdProducto())
                            .codigo(d.getProducto().getCodigo())
                            .nombre(d.getProducto().getNombre())
                            .unidadMedida(d.getProducto().getUnidadMedida())
                            .descripcion(d.getProducto().getDescripcion())
                            .build();

                        return ProformaResponseDTO.DetalleProformaResponseDTO.builder()
                            .idProformaDetalle(d.getIdProformaDetalle())    
                            .cantidad(d.getCantidad())
                            .precioUnitario(d.getPrecioUnitario())
                            .subtotal(d.getCantidad() * d.getPrecioUnitario())
                            .producto(productoDto) 
                            .build();
                })
                .collect(Collectors.toList());

        return ProformaResponseDTO.builder()
                .idProforma(p.getIdProforma())
                .codigo(p.getCodigo())
                .fechaRecepcion(p.getFechaRecepcion())
                .precioTotal(p.getPrecioTotal())
                .estado(p.getEstado())
                .idRequerimiento(p.getRequerimiento().getIdRequerimiento())
                .codigoRequerimiento(p.getRequerimiento().getCodigo())
                .proveedor(proveedorDto)
                .productos(detalles)
                .build();
    }
}