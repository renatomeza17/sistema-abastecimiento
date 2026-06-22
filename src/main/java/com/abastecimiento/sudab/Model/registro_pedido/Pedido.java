package com.abastecimiento.sudab.Model.registro_pedido; // Ajusta el package según tu arquitectura

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.abastecimiento.sudab.Model.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "registro_pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idPedido;

    @Column(unique = true, nullable = false, length = 50)
    private String codigo; // Ej: PED-2026-0001

    @Column(nullable = false, length = 500)
    private String descripcion; // Notas generales o justificación del pedido

    @Column(nullable = false, length = 30)
    private String estado; // PENDIENTE, FINALIZADO, CANCELADO

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    // Relación con el usuario (Jefe de Dependencia) que realiza la acción
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // Relación bidireccional con los detalles del pedido
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoDetalle> detalles = new ArrayList<>();
}