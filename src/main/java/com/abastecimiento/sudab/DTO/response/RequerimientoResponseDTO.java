package com.abastecimiento.sudab.DTO.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequerimientoResponseDTO {

    private Long idRequerimiento;
    private String codigo;
    private LocalDate fechaCreacion;
    private String descripcion;
    private String estado;
    private List<DetalleResponseDTO> detalles;

    @Data
    @Builder
    public static class DetalleResponseDTO {
        private Long idProducto;
        private String nombreProducto;
        private String unidadMedida;
        private Integer cantidad;
    }
}