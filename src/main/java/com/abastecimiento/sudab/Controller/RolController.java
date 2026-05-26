package com.abastecimiento.sudab.Controller;

// CAMBIA ESTOS DOS IMPORTS:
import com.abastecimiento.sudab.Model.Rol; 
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
    public List<Rol> listar() {
        return rolService.listar();
    }
}