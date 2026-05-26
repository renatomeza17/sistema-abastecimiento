package com.abastecimiento.sudab.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.abastecimiento.sudab.DTO.request.RequerimientoRequestDTO;
import com.abastecimiento.sudab.DTO.response.RequerimientoResponseDTO;
import com.abastecimiento.sudab.Service.RequerimientoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/requerimientos")
@RequiredArgsConstructor
public class RequerimientoController {

    private final RequerimientoService requerimientoService;

    // Jefe crea un nuevo requerimiento
    @PostMapping
    public ResponseEntity<RequerimientoResponseDTO> crear(
            @RequestBody RequerimientoRequestDTO dto) {
        return ResponseEntity.ok(requerimientoService.crear(dto));
    }

    // Listar todos (Jefe y Proveedor)
    @GetMapping
    public ResponseEntity<List<RequerimientoResponseDTO>> listar() {
        return ResponseEntity.ok(requerimientoService.listar());
    }

    // Listar por estado: PENDIENTE, EN_PROCESO, CERRADO
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<RequerimientoResponseDTO>> listarPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(requerimientoService.listarPorEstado(estado));
    }

    // Ver uno en detalle
    @GetMapping("/{id}")
    public ResponseEntity<RequerimientoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(requerimientoService.obtener(id));
    }

    // Director cambia estado (APROBADO / CANCELADO)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<RequerimientoResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return ResponseEntity.ok(requerimientoService.cambiarEstado(id, estado));
    }
}