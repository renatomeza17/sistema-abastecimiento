package com.abastecimiento.sudab.Model.recepcion;

import java.time.LocalDateTime;

import com.abastecimiento.sudab.Model.compra.OrdenCompra;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pedido_pendiente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoPendiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido_pendiente")
    private Long idPedidoPendiente;

    @ManyToOne
    @JoinColumn(name = "id_orden", nullable = false)
    private OrdenCompra ordenCompra;

    @Column(nullable = false)
    private String motivo;

    @Column(columnDefinition = "TEXT")
    private String observacion;

    @Column(nullable = false)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;
}