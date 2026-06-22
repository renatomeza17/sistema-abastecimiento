package com.abastecimiento.sudab.Controller;

import com.abastecimiento.sudab.DTO.request.PedidoRequestDTO;
import com.abastecimiento.sudab.DTO.response.PedidoResponseDTO;
import com.abastecimiento.sudab.Service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;

    // Crear un pedido de forma 100% segura
    @PostMapping("/crear")
    public ResponseEntity<PedidoResponseDTO> crearPedido(@RequestBody PedidoRequestDTO requestDTO) {
        PedidoResponseDTO nuevoPedido = pedidoService.crearPedido(requestDTO);
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }

    // Listar el historial basándose estrictamente en el token del usuario
    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> listarMisPedidos() {
        List<PedidoResponseDTO> historial = pedidoService.listarPedidosPorUsuario();
        return ResponseEntity.ok(historial);
    }
}