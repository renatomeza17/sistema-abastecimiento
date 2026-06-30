package com.abastecimiento.sudab.DTO.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TokenRefreshRequest {
    @JsonProperty("refreshToken")
    private String refreshToken;

    @JsonProperty("refresh_token")
    public void setRefreshTokenSnake(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}