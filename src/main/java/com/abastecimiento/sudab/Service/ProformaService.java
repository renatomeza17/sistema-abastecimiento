package com.abastecimiento.sudab.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abastecimiento.sudab.DTO.request.ProformaRequestDTO;
import com.abastecimiento.sudab.DTO.response.ProformaResponseDTO;
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

        // 1. Buscar requerimiento y proveedor
        Requerimiento req = requerimientoRepository.findById(dto.getIdRequerimiento())
                .orElseThrow(() -> new RuntimeException("Requerimiento no encontrado."));

        Proveedor proveedor = proveedorRepository.findById(dto.getIdProveedor())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado."));

        // 2. Generar código: PROF-{nombreProveedor}-{año}-{seq}
        long total = proformaRepository.count();
        String codigoProv = proveedor.getRazonSocial().toUpperCase()
                .replaceAll("\\s+", "-").substring(0, Math.min(8, proveedor.getRazonSocial().length()));
        String codigo = String.format("PROF-%s-%04d", codigoProv, total + 1);

        // 3. Construir Proforma
        Proforma proforma = new Proforma();
        proforma.setCodigo(codigo);
        proforma.setFechaRecepcion(dto.getFechaRecepcion());
        proforma.setEstado("RECIBIDA");
        proforma.setRequerimiento(req);
        proforma.setProveedor(proveedor);

        // 4. Construir detalles y calcular precio total
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

        // 5. Calcular precio total sumando subtotales
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

    // ── Elegir proforma (Jefe elige la mejor) ──────────────────────────────────

    @Transactional
    public ProformaResponseDTO elegir(Long idProforma) {
        Proforma elegida = proformaRepository.findById(idProforma)
                .orElseThrow(() -> new RuntimeException("Proforma no encontrada."));

        // Rechaza todas las demás del mismo requerimiento
        proformaRepository
            .findByRequerimiento_IdRequerimiento(elegida.getRequerimiento().getIdRequerimiento())
            .forEach(p -> {
                p.setEstado(p.getIdProforma().equals(idProforma) ? "ELEGIDA" : "RECHAZADA");
                proformaRepository.save(p);
            });

        // Cambia estado del requerimiento a EN_PROCESO
        Requerimiento req = elegida.getRequerimiento();
        req.setEstado("EN_PROCESO");
        requerimientoRepository.save(req);

        return toResponse(elegida);
    }

    // ── Mapper ─────────────────────────────────────────────────────────────────

    private ProformaResponseDTO toResponse(Proforma p) {
        List<ProformaResponseDTO.DetalleProformaResponseDTO> detalles = p.getProductos()
                .stream().map(d -> ProformaResponseDTO.DetalleProformaResponseDTO.builder()
                        .idProducto(d.getProducto().getIdProducto())
                        .nombreProducto(d.getProducto().getNombre())
                        .unidadMedida(d.getProducto().getUnidadMedida())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getCantidad() * d.getPrecioUnitario())
                        .build())
                .collect(Collectors.toList());

        return ProformaResponseDTO.builder()
                .idProforma(p.getIdProforma())
                .codigo(p.getCodigo())
                .fechaRecepcion(p.getFechaRecepcion())
                .precioTotal(p.getPrecioTotal())
                .estado(p.getEstado())
                .idRequerimiento(p.getRequerimiento().getIdRequerimiento())
                .codigoRequerimiento(p.getRequerimiento().getCodigo())
                .idProveedor(p.getProveedor().getIdProveedor())
                .razonSocialProveedor(p.getProveedor().getRazonSocial())
                .productos(detalles)
                .build();
    }
}