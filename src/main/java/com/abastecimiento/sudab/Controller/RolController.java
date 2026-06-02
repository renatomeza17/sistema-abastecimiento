package com.abastecimiento.sudab.Controller;

import com.abastecimiento.sudab.DTO.response.RolResponseDTO;
import com.abastecimiento.sudab.Service.RolService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@CrossOrigin(origins = "http://localhost:4200")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public List<RolResponseDTO> listar() {
        return rolService.listar();
    }
}