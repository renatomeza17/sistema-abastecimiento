package com.abastecimiento.sudab.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.abastecimiento.sudab.DTO.request.ProformaRequestDTO;
import com.abastecimiento.sudab.DTO.response.ProformaResponseDTO;
import com.abastecimiento.sudab.Service.ProformaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proformas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProformaController {

    private final ProformaService proformaService;

    // Proveedor envía su proforma
    @PostMapping
    public ResponseEntity<ProformaResponseDTO> crear(
            @RequestBody ProformaRequestDTO dto) {
        return ResponseEntity.ok(proformaService.crear(dto));
    }

    

    @GetMapping("/{idProforma}")
    public ResponseEntity<ProformaResponseDTO> obtenerPorId(@PathVariable Long idProforma) {
        return ResponseEntity.ok(proformaService.obtenerPorIdService(idProforma));
    }



    





    // Jefe ve todas las proformas de un requerimiento
    @GetMapping("/requerimiento/{idRequerimiento}")
    public ResponseEntity<List<ProformaResponseDTO>> porRequerimiento(
            @PathVariable Long idRequerimiento) {
        return ResponseEntity.ok(proformaService.listarPorRequerimiento(idRequerimiento));
    }






    // Proveedor ve sus propias proformas
    @GetMapping("/proveedor/{idProveedor}")
    public ResponseEntity<List<ProformaResponseDTO>> porProveedor(
            @PathVariable Long idProveedor) {
        return ResponseEntity.ok(proformaService.listarPorProveedor(idProveedor));
    }

    // Jefe elige la mejor proforma
    @PatchMapping("/{idProforma}/elegir")
    public ResponseEntity<ProformaResponseDTO> elegir(
            @PathVariable Long idProforma) {
        return ResponseEntity.ok(proformaService.elegir(idProforma));
    }


    


    // Endpoint para que la interfaz de Órdenes jale las proformas listas para procesar
    @GetMapping("/elegidas")
    public ResponseEntity<List<ProformaResponseDTO>> obtenerProformasElegidas() {
        return ResponseEntity.ok(proformaService.listarProformasElegidas());
    }
}