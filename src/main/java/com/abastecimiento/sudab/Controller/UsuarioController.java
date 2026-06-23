package com.abastecimiento.sudab.Controller;

import com.abastecimiento.sudab.Model.Usuario; 
import com.abastecimiento.sudab.Service.UsuarioService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
public ResponseEntity<List<Usuario>> getAllUsuarios() {
    // Cambiamos getUsuarios() por listarTodos()
    return ResponseEntity.ok(usuarioService.listarTodos()); 
    }
}