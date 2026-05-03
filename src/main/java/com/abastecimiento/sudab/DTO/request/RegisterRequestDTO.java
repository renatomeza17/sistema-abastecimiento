package com.abastecimiento.sudab.DTO.request;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.abastecimiento.sudab.Model.Persona;
import com.abastecimiento.sudab.Model.Usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    // Datos de Usuario
    private String username;
    private String email;
    private String password;
    private PersonaDTO persona;
    private List<Long> idsRoles; // IDs de los roles a asignar (ej: [2, 4])



    public Usuario toEntity(PasswordEncoder passwordEncoder, Persona personaEntidad) {
        Usuario usuario = new Usuario();
        usuario.setUsername(this.username);
        usuario.setPassword(passwordEncoder.encode(this.password));
        usuario.setEmail(this.email);
        usuario.setPersona(personaEntidad);
        return usuario;
    }

}
