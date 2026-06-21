package com.abastecimiento.sudab.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abastecimiento.sudab.DTO.request.KardexMovimientoRequestDTO;
import com.abastecimiento.sudab.DTO.request.KardexRequestDTO;
import com.abastecimiento.sudab.DTO.response.KardexMovimientoResponseDTO;
import com.abastecimiento.sudab.DTO.response.KardexResponseDTO;
import com.abastecimiento.sudab.Model.Producto;
import com.abastecimiento.sudab.Model.inventario.Kardex;
// import com.abastecimiento.sudab.Model.inventario.KardexMovimiento;
import com.abastecimiento.sudab.Repository.KardexMovimientoRepository;
import com.abastecimiento.sudab.Repository.ProductoRepository;
import com.abastecimiento.sudab.Service.KardexService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/kardex")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class KardexController {

    private final KardexService kardexService;
    private final KardexMovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;

    @PostMapping("/nuevo-asiento")  
    public ResponseEntity<Kardex> crearAsiento(@RequestBody KardexRequestDTO dto) {
        return new ResponseEntity<>(kardexService.registrarNuevoAsiento(dto), HttpStatus.CREATED);
    }

    // Para ver la ficha técnica histórica de un producto en el Frontend
    // @GetMapping("/historial/{idKardex}")
    // public ResponseEntity<List<KardexMovimiento>> obtenerHistorial(@PathVariable Long idKardex) {
    //     return ResponseEntity.ok(movimientoRepository.findByKardexIdKardexOrderByFechaMovimientoDesc(idKardex));
    // }


    //KARDEX
    @GetMapping("/productos-disponibles")
    public ResponseEntity<List<Producto>> obtenerProductosSinKardex() {
        // Te servirá para llenar el combobox/select en el formulario de la HU11 en Angular
        return ResponseEntity.ok(productoRepository.findProductosSinKardex());
    }

    @GetMapping
    public ResponseEntity<List<KardexResponseDTO>> obtenerInventarioGeneral() {
        return ResponseEntity.ok(kardexService.listarTodoElKardex());
    }

    @GetMapping("/{idKardex}/movimientos")
    public ResponseEntity<List<KardexMovimientoResponseDTO>> obtenerHistorialMovimientos(@PathVariable Long idKardex) {
        return ResponseEntity.ok(kardexService.obtenerMovimientosPorKardex(idKardex));
    }

    
    @PostMapping("/movimiento")
    public ResponseEntity<String> crearMovimientoManual(@RequestBody KardexMovimientoRequestDTO request) {
        kardexService.registrarMovimiento(
            request.getIdProducto(),
            request.getCantidad(),
            request.getTipoMovimiento(),
            request.getDocumentoReferencia(),
            request.getObservaciones()
        );
        return ResponseEntity.ok("✅ Movimiento registrado y stock actualizado con éxito en Neon DB.");
    }





}
