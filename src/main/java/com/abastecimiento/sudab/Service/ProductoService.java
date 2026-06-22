package com.abastecimiento.sudab.Service;

import com.abastecimiento.sudab.DTO.response.ProductoResponseDTO;
import com.abastecimiento.sudab.Model.Producto;
import com.abastecimiento.sudab.Repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductosActivos() {
        List<Producto> productos = productoRepository.findByActivoTrue();
        
        // Usamos tu DTO existente mapeando todos sus atributos reales
        return productos.stream()
                .map(prod -> ProductoResponseDTO.builder()
                        .idProducto(prod.getIdProducto())
                        .codigo(prod.getCodigo())
                        .nombre(prod.getNombre())
                        .descripcion(prod.getDescripcion())
                        .unidadMedida(prod.getUnidadMedida())
                        .activo(prod.getActivo()) // Mapea el boolean nativo de tu modelo
                        .build())
                .collect(Collectors.toList());
    }
}