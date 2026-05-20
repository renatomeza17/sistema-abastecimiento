package com.abastecimiento.sudab.Model.compra;


import java.time.LocalDate;

import java.util.List;



import com.abastecimiento.sudab.Model.Proforma;
import com.abastecimiento.sudab.Model.Proveedor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "ordenes_compra")
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrden;

    @Column(unique = true, nullable = false)
    private String codigo; // Ej: OC-2026-0001

    @Column(nullable = false, length = 100) 
    private String descripcion;

     @Column(nullable = false)
    private LocalDate fechaCreacion;

    @Column(nullable = false)
    private LocalDate fechaEntrega;

    @Column(nullable = false)
    private String estado;

    
    @Column(nullable = false, precision = 10, scale = 2)
    private Double montoTotal;  




    @OneToOne
    @JoinColumn(name = "id_proforma",nullable = false)
    private Proforma proforma;

    @ManyToOne
    @JoinColumn(name = "id_proveedor",nullable = false)
    private Proveedor proveedor;


    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL)
    private List<OrdenCompraDetalle> detalles;






}



