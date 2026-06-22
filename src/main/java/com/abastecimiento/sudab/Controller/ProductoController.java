package com.abastecimiento.sudab.Controller;

import com.abastecimiento.sudab.DTO.response.ProductoResponseDTO;
import com.abastecimiento.sudab.Service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;

    // Endpoint para nutrir el combobox/select de Angular
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> obtenerCatalogo() {
        List<ProductoResponseDTO> catalogo = productoService.listarProductosActivos();
        return ResponseEntity.ok(catalogo);
    }
}