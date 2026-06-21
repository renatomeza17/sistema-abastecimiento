package com.abastecimiento.sudab.Controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.abastecimiento.sudab.Model.recepcion.PedidoPendiente;
import com.abastecimiento.sudab.Service.PedidoPendienteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pedidos-pendientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PedidoPendienteController {

    private final PedidoPendienteService pedidoPendienteService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> registrar(
            @RequestParam Long idOrden,
            @RequestParam String motivo
    ) {
        PedidoPendiente pedido = pedidoPendienteService.registrar(idOrden, motivo);

        return ResponseEntity.ok(convertirADTO(pedido));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        List<Map<String, Object>> pedidos = pedidoPendienteService.listar()
                .stream()
                .map(this::convertirADTO)
                .toList();

        return ResponseEntity.ok(pedidos);
    }

    @PutMapping("/{id}/resolver")
    public ResponseEntity<Map<String, Object>> resolver(@PathVariable Long id) {
        PedidoPendiente pedido = pedidoPendienteService.resolver(id);

        return ResponseEntity.ok(convertirADTO(pedido));
    }

    private Map<String, Object> convertirADTO(PedidoPendiente pedido) {
        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("idPedidoPendiente", pedido.getIdPedidoPendiente());
        dto.put("motivo", pedido.getMotivo());
        dto.put("observacion", pedido.getObservacion());
        dto.put("estado", pedido.getEstado());
        dto.put("fechaRegistro", pedido.getFechaRegistro());
        dto.put("fechaResolucion", pedido.getFechaResolucion());

        Map<String, Object> orden = new LinkedHashMap<>();

        if (pedido.getOrdenCompra() != null) {
            orden.put("idOrden", pedido.getOrdenCompra().getIdOrden());
            orden.put("codigo", pedido.getOrdenCompra().getCodigo());
            orden.put("estado", pedido.getOrdenCompra().getEstado());

            Map<String, Object> proveedor = new LinkedHashMap<>();

            if (pedido.getOrdenCompra().getProveedor() != null) {
                proveedor.put("razonSocial", pedido.getOrdenCompra().getProveedor().getRazonSocial());
                proveedor.put("ruc", pedido.getOrdenCompra().getProveedor().getRuc());
            } else {
                proveedor.put("razonSocial", "Proveedor no asignado");
                proveedor.put("ruc", "");
            }

            orden.put("proveedor", proveedor);
        }

        dto.put("ordenCompra", orden);

        return dto;
    }
}