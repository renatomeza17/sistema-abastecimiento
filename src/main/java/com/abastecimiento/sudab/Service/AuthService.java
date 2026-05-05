package com.abastecimiento.sudab.Service;

import com.abastecimiento.sudab.DTO.request.LoginRequestDTO;
import com.abastecimiento.sudab.DTO.request.ModuloDTO;
import com.abastecimiento.sudab.DTO.request.RegisterRequestDTO;
import com.abastecimiento.sudab.DTO.response.LoginResponseDTO;
import com.abastecimiento.sudab.Model.Persona;
import com.abastecimiento.sudab.Model.Rol;
import com.abastecimiento.sudab.Model.Usuario;
import com.abastecimiento.sudab.Repository.*;
import com.abastecimiento.sudab.Security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final RolRepository rolRepository;
    private final RolModuloRepository rolModuloRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // ─── REGISTER ──────────────────────────────────────────────────────────────

    @Transactional
    public String register(RegisterRequestDTO request) {

        // 1. Verificar que el username no exista
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El username ya está en uso: " + request.getUsername());
        }

        // 2. Verificar que el DNI no esté registrado
        if (personaRepository.findByNumDocumento(request.getPersona().toEntity().getNumDocumento()).isPresent()) {
            throw new RuntimeException("El documento ya está registrado.");
        }

        // 3. Guardar Persona
        Persona persona = request.getPersona().toEntity();
        personaRepository.save(persona);

        // 4. Construir y guardar Usuario
        Usuario usuario = request.toEntity(passwordEncoder, persona);

        // 5. Asignar roles
        if (request.getIdsRoles() != null && !request.getIdsRoles().isEmpty()) {
            var roles = new HashSet<Rol>();
            for (Long idRol : request.getIdsRoles()) {
                Rol rol = rolRepository.findById(idRol)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + idRol));
                roles.add(rol);
            }
            usuario.setRoles(roles);
        }

        usuarioRepository.save(usuario);
        return "Usuario registrado exitosamente.";
    }

    // ─── LOGIN ──────────────────────────────────────────────────────────────────

    public LoginResponseDTO login(LoginRequestDTO request) {

        // 1. Autenticar credenciales (lanza excepción si falla)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 2. Cargar usuario de la BD
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // 3. Generar token JWT
        String token = jwtUtil.generateToken(usuario.getUsername());

        // 4. Obtener nombres de roles
        List<String> roles = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .collect(Collectors.toList());

        // 5. Obtener módulos accesibles (union de todos sus roles)
        List<ModuloDTO> modulos = usuario.getRoles().stream()
                .flatMap(rol -> rolModuloRepository.findByRol(rol).stream())
                .map(rolModulo -> ModuloDTO.builder()
                        .descripcion(rolModulo.getModulo().getDescripcion())
                        .url(rolModulo.getModulo().getUrl())
                        .build())
                .distinct()
                .collect(Collectors.toList());

        // 6. Armar nombre completo
        String nombreCompleto = usuario.getPersona().getNombres()
                + " " + usuario.getPersona().getApellidoPaterno()
                + " " + usuario.getPersona().getApellidoMaterno();

        return LoginResponseDTO.builder()
                .username(usuario.getUsername())
                .nombreCompleto(nombreCompleto)
                .roles(roles)
                .modulos(modulos)
                .token(token)
                .build();
    }
}