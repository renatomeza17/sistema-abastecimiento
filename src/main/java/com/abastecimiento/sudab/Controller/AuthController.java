package com.abastecimiento.sudab.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abastecimiento.sudab.DTO.request.LoginRequestDTO;
import com.abastecimiento.sudab.DTO.request.RegisterRequestDTO;
import com.abastecimiento.sudab.DTO.response.LoginResponseDTO;
import com.abastecimiento.sudab.Service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
// El endpoint base es /api/auth, así que login será /api/auth/login y register será /api/auth/register
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") 
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO request) {
        String mensaje = authService.register(request);
        return ResponseEntity.ok(mensaje);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}