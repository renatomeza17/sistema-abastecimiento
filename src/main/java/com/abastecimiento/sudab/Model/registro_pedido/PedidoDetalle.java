package com.abastecimiento.sudab.Model.registro_pedido; // Ajusta el package según tu arquitectura

import com.abastecimiento.sudab.Model.Producto; // Importa tu modelo Producto existente
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "detalle_registro_pedidos")
public class PedidoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_pedido")
    private Long idDetallePedido;

    // Vinculación con la cabecera del pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    // Vinculación con el catálogo de productos existente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(length = 255)
    private String observacionEspecifica; // Por si algún producto requiere una especificación única (opcional)
}