package com.abastecimiento.sudab.Model.inventario;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="kardex_movimiento")
public class KardexMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long IdMovimiento;

    @ManyToOne
    @JoinColumn(name = "id_kardex", nullable = false)
    private Kardex kardex;


    @Column(length = 30)
    private String tipoMovimiento;

    @Column
    private Integer cantidad;

    @Column
    private Integer saldoStock; // El stock resultante después de este movimiento

    @Column
    private LocalDateTime fechaMovimiento;

    
    // Documento de origen para auditoría del Estado Peruano
    @Column(length = 40)
    private String documentoReferencia; // Ej: "OC-2026-0012" o "PECOSA-2026-0005"

    @Column(length = 100)
    private String observaciones;

}
