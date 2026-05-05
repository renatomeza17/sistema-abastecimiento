package com.abastecimiento.sudab.DTO.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.abastecimiento.sudab.Model.Persona;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDTO {

    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombres;
    private String sexo;
    private LocalDate fechaNacimiento;
    private String tipoDocumento;
    private String numDocumento;
    private String direccion;
    private String telefono;

    public Persona toEntity() {
        return Persona.builder()
                .nombres(this.nombres)
                .apellidoPaterno(this.apellidoPaterno)
                .apellidoMaterno(this.apellidoMaterno)
                .numDocumento(this.numDocumento)
                .direccion(this.direccion)
                .telefono(this.telefono)
                .sexo(this.sexo)
                .tipoDocumento(this.tipoDocumento)
                .fechaNacimiento(this.fechaNacimiento)
                .createdAt(LocalDateTime.now())
                .build();
    }
}