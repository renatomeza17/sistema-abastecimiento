package com.abastecimiento.sudab.DTO.response;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PedidoResponseDTO {
    
    private Long idPedido;
    private String codigo;
    private String descripcion;
    private String estado;
    private LocalDate fechaCreacion;
    
    // Datos opcionales del solicitante para mostrar en la interfaz
    private String nombreSolicitante; 
    private String nombreDependencia; 
    
    private List<DetallePedidoResponseDTO> detalles;

    @Data
    @Builder
    public static class DetallePedidoResponseDTO {
        private Long idDetallePedido;
        private Long idProducto;
        private String nombreProducto; // Útil para que el frontend no tenga que buscar el nombre
        private String unidadMedida;   // Útil para la tabla del frontend
        private Integer cantidad;
        private String observacionEspecifica;
    }
}