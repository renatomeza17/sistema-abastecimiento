package com.abastecimiento.sudab.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuloDTO {
    private String descripcion;
    private String url;
    private String puedeCrear;
    private String puedeLeer;
    private String puedeActualizar;
    private String puedeEliminar;
}
