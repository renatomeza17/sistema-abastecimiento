package com.abastecimiento.sudab.Service;

import com.abastecimiento.sudab.DTO.request.ModuloDTO;
import com.abastecimiento.sudab.DTO.response.RolResponseDTO;
import com.abastecimiento.sudab.Model.Rol;
import com.abastecimiento.sudab.Repository.RolModuloRepository;
import com.abastecimiento.sudab.Repository.RolRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private RolModuloRepository rolModuloRepository;

    public List<RolResponseDTO> listar() {
        List<Rol> roles = rolRepository.findAll();

        return roles.stream().map(rol -> {
            List<ModuloDTO> modulos = rolModuloRepository.findByRol(rol)
                    .stream()
                    .map(rm -> ModuloDTO.builder()
                            .descripcion(rm.getModulo().getDescripcion())
                            .url(rm.getModulo().getUrl())
                            .puedeCrear(rm.getPuedeCrear())
                            .puedeLeer(rm.getPuedeLeer())
                            .puedeActualizar(rm.getPuedeActualizar())
                            .puedeEliminar(rm.getPuedeEliminar())
                            .build())
                    .distinct()
                    .collect(Collectors.toList());

            return RolResponseDTO.builder()
                    .idRol(rol.getIdRol())
                    .nombre(rol.getNombre())
                    .descripcion(rol.getDescripcion())
                    .modulos(modulos)
                    .build();
        }).collect(Collectors.toList());
    }
}