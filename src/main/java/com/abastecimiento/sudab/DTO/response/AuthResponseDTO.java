package com.abastecimiento.sudab.DTO.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {
    private String token;
    private String refreshToken;
}
