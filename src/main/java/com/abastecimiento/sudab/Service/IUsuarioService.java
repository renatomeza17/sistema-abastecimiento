package com.abastecimiento.sudab.Service;

import java.util.List;


import com.abastecimiento.sudab.DTO.request.RegisterRequestDTO;
import com.abastecimiento.sudab.Model.Usuario;

public interface IUsuarioService {
   // El administrador registra un usuario
    void registrar(RegisterRequestDTO registerRequest);
    
    // Métodos que necesitarás para tus pantallas de gestión
    List<Usuario> listarTodos();
    void eliminar(Long id);

}
