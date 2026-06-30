package com.abastecimiento.sudab.Service;

import com.abastecimiento.sudab.DTO.request.LoginRequestDTO;
import com.abastecimiento.sudab.DTO.request.ModuloDTO;
import com.abastecimiento.sudab.DTO.request.RegisterRequestDTO;
import com.abastecimiento.sudab.DTO.response.AuthResponseDTO;
import com.abastecimiento.sudab.DTO.response.LoginResponseDTO;
import com.abastecimiento.sudab.Model.Modulo;
import com.abastecimiento.sudab.Model.Persona;
import com.abastecimiento.sudab.Model.Rol;
import com.abastecimiento.sudab.Model.Usuario;
import com.abastecimiento.sudab.Repository.*;
import com.abastecimiento.sudab.Security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("El username ya está en uso: " + request.getUsername());
        }

        if (request.getEmail() != null && usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }

        if (request.getPersona() != null
                && personaRepository.findByNumDocumento(request.getPersona().getNumDocumento()).isPresent()) {
            throw new IllegalArgumentException("El documento ya está registrado.");
        }

        Persona persona = request.getPersona().toEntity();
        personaRepository.save(persona);

        Usuario usuario = request.toEntity(passwordEncoder, persona);

        if (request.getIdsRoles() != null && !request.getIdsRoles().isEmpty()) {
            var roles = new HashSet<Rol>();
            for (Long idRol : request.getIdsRoles()) {
                Rol rol = rolRepository.findById(idRol)
                        .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + idRol));
                roles.add(rol);
            }
            usuario.setRoles(roles);
        }

        usuarioRepository.save(usuario);

        List<Modulo> modules = usuario.getRoles().stream()
                .flatMap(rol -> rolModuloRepository.findByRol(rol).stream())
                .map(rolModulo -> rolModulo.getModulo())
                .distinct()
                .collect(Collectors.toList());

        String token = jwtUtil.generateToken(usuario, modules);
        String refreshToken = jwtUtil.generateRefreshToken(usuario);

        return AuthResponseDTO.builder()
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        String id = request.getIdentificador();

        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("El identificador es obligatorio.");
        }

        Usuario usuario = usuarioRepository.findByUsername(id)
                .or(() -> usuarioRepository.findByEmail(id))
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas o usuario no encontrado."));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuario.getUsername(), request.getPassword())
        );

        List<Modulo> modules = usuario.getRoles().stream()
                .flatMap(rol -> rolModuloRepository.findByRol(rol).stream())
                .map(rolModulo -> rolModulo.getModulo())
                .distinct()
                .collect(Collectors.toList());

        String token = jwtUtil.generateToken(usuario, modules);
        String refreshToken = jwtUtil.generateRefreshToken(usuario);

        List<String> roles = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .collect(Collectors.toList());

        List<ModuloDTO> modulos = modules.stream()
                .map(mod -> ModuloDTO.builder()
                        .descripcion(mod.getDescripcion())
                        .url(mod.getUrl())
                        .build())
                .collect(Collectors.toList());

        String nombreCompleto = usuario.getPersona().getNombres()
                + " " + usuario.getPersona().getApellidoPaterno()
                + " " + usuario.getPersona().getApellidoMaterno();

        return LoginResponseDTO.builder()
                .username(usuario.getUsername())
                .nombreCompleto(nombreCompleto)
                .roles(roles)
                .modulos(modulos)
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponseDTO refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("El token de refresco es obligatorio.");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));

        if (!jwtUtil.isTokenValid(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
        throw new BadCredentialsException("Token de refresco inválido.");
        }

        List<Modulo> modules = usuario.getRoles().stream()
                .flatMap(rol -> rolModuloRepository.findByRol(rol).stream())
                .map(rolModulo -> rolModulo.getModulo())
                .distinct()
                .collect(Collectors.toList());

        String newToken = jwtUtil.generateToken(usuario, modules);
        String newRefreshToken = jwtUtil.generateRefreshToken(usuario);

        return AuthResponseDTO.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
