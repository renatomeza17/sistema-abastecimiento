package com.abastecimiento.sudab.DTO.request;

import lombok.Data;

@Data
public class OrdenRequestDTO {
    private Long idProforma;

    //DATOS QUE DIGITA EL USUARIO
    private String fechaEntrega;
    private String lugarEntrega;
    private String observaciones; 
    private String formaPago;
    private String plazoEntrega;
    private String garantia;

}
