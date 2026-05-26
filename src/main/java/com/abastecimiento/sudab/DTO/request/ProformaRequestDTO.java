package com.abastecimiento.sudab.DTO.request;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ProformaRequestDTO {

    private Long idRequerimiento;   // A qué requerimiento responde
    private Long idProveedor;       // Quién envía la proforma
    private LocalDate fechaRecepcion;

    private List<DetalleProformaRequestDTO> productos;

    @Data
    public static class DetalleProformaRequestDTO {
        private Long idProducto;
        private Integer cantidad;
        private Double precioUnitario;
    }
}