package com.abastecimiento.sudab.DTO.request;

import java.util.List;
import lombok.Data;

@Data
public class PedidoRequestDTO {
    
    private String descripcion;
    private List<DetallePedidoRequestDTO> detalles;

    @Data
    public static class DetallePedidoRequestDTO {
        private Long idProducto;
        private Integer cantidad;
        private String observacionEspecifica; // Opcional, por si el jefe necesita especificar algo del producto
    }
}