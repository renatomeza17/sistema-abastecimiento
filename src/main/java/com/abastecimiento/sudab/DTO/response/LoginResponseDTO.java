package com.abastecimiento.sudab.DTO.response;

import java.util.List;

import com.abastecimiento.sudab.DTO.request.ModuloDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {
        private String username;
        private String nombreCompleto;
        private List<String> roles;
        private List<ModuloDTO> modulos;
        private String token;
        private String refreshToken;
}
