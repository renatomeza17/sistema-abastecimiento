package com.abastecimiento.sudab.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.abastecimiento.sudab.DTO.request.RegisterRequestDTO;

import com.abastecimiento.sudab.Model.Persona;
import com.abastecimiento.sudab.Model.Rol;
import com.abastecimiento.sudab.Model.Usuario;

import com.abastecimiento.sudab.Repository.UsuarioRepository;

@Service
public class UsuarioService implements IUsuarioService{

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // @Autowired
    // private RolModuloRepository rolModuloRepository;

    @Autowired
    private com.abastecimiento.sudab.Repository.PersonaRepository personaRepository;
    
    @Autowired
    private com.abastecimiento.sudab.Repository.RolRepository rolRepository;
    
    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public void registrar(RegisterRequestDTO request) {
        // 1. Guardar Persona
        Persona persona = request.getPersona().toEntity();
        persona = personaRepository.save(persona);

        // 2. Guardar Usuario vinculado
        Usuario usuario = request.toEntity(passwordEncoder, persona);
        
        // 3. Asignar Roles
        if (request.getIdsRoles() != null) {
            Set<Rol> roles = new HashSet<>(rolRepository.findAllById(request.getIdsRoles()));
            usuario.setRoles(roles);
        }

        usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

}
