package com.abastecimiento.sudab.Model.inventario;

import java.time.LocalDate;

import com.abastecimiento.sudab.Model.Producto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="kardex")
public class Kardex {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idKardex;

    @OneToOne
    @JoinColumn(name="id_producto",unique = true,nullable = false)
    private Producto producto;

    @Column
    private Integer stockActual;

    @Column
    private Integer stockMinimo;

    @Column(length = 20)
    private String ubicacionAlmacen; // Ej: "Estante B-4"

    @Column
    private LocalDate fechaApertura;

    @Column(length = 100)
    private String caracteristicas;

}
