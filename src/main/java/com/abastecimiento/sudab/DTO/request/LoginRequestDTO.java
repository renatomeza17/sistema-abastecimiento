package com.abastecimiento.sudab.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class LoginRequestDTO {

    private String identificador; // puede ser email o username
    private String password;

}
