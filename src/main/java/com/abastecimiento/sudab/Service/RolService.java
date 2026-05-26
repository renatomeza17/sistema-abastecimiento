package com.abastecimiento.sudab.Service;

import com.abastecimiento.sudab.Model.Rol;
import com.abastecimiento.sudab.Repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public List<Rol> listar() {
        return rolRepository.findAll();
    }
}