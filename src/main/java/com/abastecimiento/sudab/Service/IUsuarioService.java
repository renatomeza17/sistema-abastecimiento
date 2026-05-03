package com.abastecimiento.sudab.Service;

import com.abastecimiento.sudab.DTO.request.LoginRequestDTO;
import com.abastecimiento.sudab.DTO.request.RegisterRequestDTO;
import com.abastecimiento.sudab.DTO.response.LoginResponseDTO;

public interface IUsuarioService {
    // Recibe un Request y devuelve un Response
    LoginResponseDTO autenticar(LoginRequestDTO loginRequest);
    void registrar(RegisterRequestDTO registerRequest);

}
