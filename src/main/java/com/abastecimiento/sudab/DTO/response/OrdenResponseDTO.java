package com.abastecimiento.sudab.DTO.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;


@Data
public class OrdenResponseDTO {
    private Long idOrden;
    private String codigo;
    private LocalDate fechaCreacion;
    private Double montoTotal;
    private String estado;
    
    // Datos aplanados del proveedor
    private String nombreProveedor;
    private String rucProveedor;
    
    // El "salto" de trazabilidad que va desde la OC -> Proforma -> Requerimiento
    private String codigoRequerimiento; 
    
    // La lista de sus productos usando su propio DTO detalle
    private List<OrdenDetalleDTO> detalles;


    

}
