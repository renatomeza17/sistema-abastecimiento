package com.abastecimiento.sudab.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abastecimiento.sudab.DTO.request.RequerimientoRequestDTO;
import com.abastecimiento.sudab.DTO.response.RequerimientoResponseDTO;
import com.abastecimiento.sudab.Model.Producto;
import com.abastecimiento.sudab.Model.requerimiento.Requerimiento;
import com.abastecimiento.sudab.Model.requerimiento.RequerimientoDetalle;
import com.abastecimiento.sudab.Repository.ProductoRepository;
import com.abastecimiento.sudab.Repository.RequerimientoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequerimientoService {

    private final RequerimientoRepository requerimientoRepository;
    private final ProductoRepository productoRepository;

    // ── Crear ──────────────────────────────────────────────────────────────────

    @Transactional
    public RequerimientoResponseDTO crear(RequerimientoRequestDTO dto) {

        // 1. Generar código automático: REQ-2026-0001
        long total = requerimientoRepository.count();
        String codigo = String.format("REQ-%d-%04d", LocalDate.now().getYear(), total + 1);

        // 2. Construir entidad
        Requerimiento req = new Requerimiento();
        req.setCodigo(codigo);
        req.setDescripcion(dto.getDescripcion());
        req.setFechaCreacion(LocalDate.now());
        req.setEstado("PENDIENTE"); // Estado inicial siempre PENDIENTE

        // 3. Construir detalles
        List<RequerimientoDetalle> detalles = dto.getDetalles().stream().map(d -> {
            Producto producto = productoRepository.findById(d.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + d.getIdProducto()));

            RequerimientoDetalle detalle = new RequerimientoDetalle();
            detalle.setRequerimiento(req);
            detalle.setProducto(producto);
            detalle.setCantidad(d.getCantidad());
            return detalle;
        }).collect(Collectors.toList());

        req.setDetalles(detalles);
        requerimientoRepository.save(req);

        return toResponse(req);
    }

    // ── Listar todos ───────────────────────────────────────────────────────────

    public List<RequerimientoResponseDTO> listar() {
        return requerimientoRepository.findAll()
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }




    

    // ── Listar por estado ──────────────────────────────────────────────────────

    public List<RequerimientoResponseDTO> listarPorEstado(String estado) {
        return requerimientoRepository.findByEstado(estado.toUpperCase())
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }







    // ── Obtener por ID ─────────────────────────────────────────────────────────

    public RequerimientoResponseDTO obtener(Long id) {
        Requerimiento req = requerimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requerimiento no encontrado: " + id));
        return toResponse(req);
    }

    // ── Cambiar estado ─────────────────────────────────────────────────────────
    // Usado por el Director para aprobar/cancelar

    @Transactional
    public RequerimientoResponseDTO cambiarEstado(Long id, String nuevoEstado) {
        Requerimiento req = requerimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requerimiento no encontrado: " + id));
        req.setEstado(nuevoEstado.toUpperCase());
        requerimientoRepository.save(req);
        return toResponse(req);
    }

    // ── Mapper ─────────────────────────────────────────────────────────────────

    private RequerimientoResponseDTO toResponse(Requerimiento req) {
        List<RequerimientoResponseDTO.DetalleResponseDTO> detallesDTO = req.getDetalles()
                .stream().map(d -> RequerimientoResponseDTO.DetalleResponseDTO.builder()
                        .idProducto(d.getProducto().getIdProducto())
                        .nombreProducto(d.getProducto().getNombre())
                        .unidadMedida(d.getProducto().getUnidadMedida())
                        .cantidad(d.getCantidad())
                        .build())
                .collect(Collectors.toList());

        return RequerimientoResponseDTO.builder()
                .idRequerimiento(req.getIdRequerimiento())
                .codigo(req.getCodigo())
                .fechaCreacion(req.getFechaCreacion())
                .descripcion(req.getDescripcion())
                .estado(req.getEstado())
                .detalles(detallesDTO)
                .build();
    }
}