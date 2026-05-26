package com.abastecimiento.sudab.DTO.request;

import java.util.List;

import lombok.Data;

@Data
public class RequerimientoRequestDTO {

    private String descripcion;

    // Lista de productos que se necesitan
    private List<DetalleRequestDTO> detalles;

    @Data
    public static class DetalleRequestDTO {
        private Long idProducto;
        private Integer cantidad;
    }
}