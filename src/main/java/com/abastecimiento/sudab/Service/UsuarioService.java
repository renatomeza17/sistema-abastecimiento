package com.abastecimiento.sudab.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abastecimiento.sudab.DTO.request.LoginRequestDTO;
import com.abastecimiento.sudab.DTO.request.ModuloDTO;
import com.abastecimiento.sudab.DTO.request.RegisterRequestDTO;
import com.abastecimiento.sudab.DTO.response.LoginResponseDTO;
import com.abastecimiento.sudab.Model.Persona;
import com.abastecimiento.sudab.Model.Rol;
import com.abastecimiento.sudab.Model.Usuario;
import com.abastecimiento.sudab.Repository.RolModuloRepository;
import com.abastecimiento.sudab.Repository.UsuarioRepository;

@Service
public class UsuarioService implements IUsuarioService{

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RolModuloRepository rolModuloRepository;

    @Autowired
    private com.abastecimiento.sudab.Repository.PersonaRepository personaRepository;
    
    @Autowired
    private com.abastecimiento.sudab.Repository.RolRepository rolRepository;
    
    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    
    @Override
    public LoginResponseDTO autenticar(LoginRequestDTO request) {
        // 1. Buscar al usuario
        Usuario user = usuarioRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        // 2. Filtrar módulos según sus roles (Menú Dinámico)
        List<ModuloDTO> menu = user.getRoles().stream()
            .flatMap(rol -> rolModuloRepository.findByRol(rol).stream())
            .filter(rm -> rm.getModulo().getActivo().equalsIgnoreCase("S"))
            .map(rm -> ModuloDTO.builder()
                .descripcion(rm.getModulo().getDescripcion())
                .url(rm.getModulo().getUrl())
                .build())
            .distinct()
            .collect(Collectors.toList());

        // 3. Retornar el Response DTO
        return LoginResponseDTO.builder()
            .username(user.getUsername())
            .nombreCompleto(user.getPersona().getNombres() + " " + user.getPersona().getApellidoPaterno())
            .roles(user.getRoles().stream().map(Rol::getNombre).collect(Collectors.toList()))
            .modulos(menu)
            .token("jwt-token-generado") // Simulado por ahora
            .build();
    }


    @Override
    public void registrar(RegisterRequestDTO request) {
    // 1. Convertimos el DTO de persona a Entidad y guardamos
    Persona persona = request.getPersona().toEntity();
    Persona personaGuardada = personaRepository.save(persona);

    // 2. Convertimos el Request a Entidad Usuario vinculando la persona
    Usuario usuario = request.toEntity(passwordEncoder, personaGuardada);

    // 3. Asignamos los roles (Data Maestra)
    Set<Rol> roles = new HashSet<>(rolRepository.findAllById(request.getIdsRoles()));
    usuario.setRoles(roles);

    usuarioRepository.save(usuario);
}

}
