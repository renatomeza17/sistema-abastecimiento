package com.abastecimiento.sudab.DTO.response;

import java.util.List;

import com.abastecimiento.sudab.DTO.request.ModuloDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RolResponseDTO {
    private Long idRol;
    private String nombre;
    private String descripcion;
    private List<ModuloDTO> modulos;
}