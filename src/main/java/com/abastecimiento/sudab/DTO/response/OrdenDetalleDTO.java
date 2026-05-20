package com.abastecimiento.sudab.DTO.response;

import lombok.Data;

@Data
public class OrdenDetalleDTO {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;


}
