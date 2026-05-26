package com.abastecimiento.sudab.DTO.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProformaResponseDTO {

    private Long idProforma;
    private String codigo;
    private LocalDate fechaRecepcion;
    private Double precioTotal;
    private String estado;

    // Datos del requerimiento al que responde
    private Long idRequerimiento;
    private String codigoRequerimiento;

    // Datos del proveedor
    private Long idProveedor;
    private String razonSocialProveedor;

    private List<DetalleProformaResponseDTO> productos;

    @Data
    @Builder
    public static class DetalleProformaResponseDTO {
        private Long idProducto;
        private String nombreProducto;
        private String unidadMedida;
        private Integer cantidad;
        private Double precioUnitario;
        private Double subtotal; // cantidad * precioUnitario
    }
}