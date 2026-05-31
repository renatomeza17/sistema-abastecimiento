package com.abastecimiento.sudab.Model.compra;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.abastecimiento.sudab.Model.Proveedor;
import com.abastecimiento.sudab.Model.requerimiento.Proforma;

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

    
    @Column(nullable = false)
    private Double montoTotal;  




    @OneToOne
    @JoinColumn(name = "id_proforma",nullable = false)
    private Proforma proforma;

    @ManyToOne
    @JoinColumn(name = "id_proveedor",nullable = false)
    private Proveedor proveedor;


    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL)
    private List<OrdenCompraDetalle> detalles;



    
    //ATRIBUTOS DEL REQUEST 
    
    @Column(name = "lugar_entrega", length = 255)
    private String lugarEntrega;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "forma_pago", length = 100)
    private String formaPago;

    @Column(name = "plazo_entrega", length = 100)
    private String plazoEntrega;

    @Column(name = "garantia", length = 100)
    private String garantia;


    //APROBACIÓN Y FIRMA

    @Column(name = "fecha_autorizacion")
    private LocalDateTime fechaAutorizacion;

    @Column(name = "firma_digital_hash", length = 500)
    private String firmaDigitalHash; // Hash de auditoría de la firma

    @Column(name = "autorizado_por")
    private String autorizadoPor; // Username del Director






}



