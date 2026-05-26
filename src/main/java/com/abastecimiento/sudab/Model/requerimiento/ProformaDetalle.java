package com.abastecimiento.sudab.Model.requerimiento;



import com.abastecimiento.sudab.Model.Producto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@Table(name = "proforma_detalle")
public class ProformaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProformaDetalle;

    @ManyToOne
    @JoinColumn(name = "id_proforma",nullable = false)
    private Proforma proforma;

    @ManyToOne
    @JoinColumn(name = "id_producto",nullable = false)
    private Producto producto;

    @Column(name = "cantidad",nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario",nullable = false)   
    private Double precioUnitario;

}
