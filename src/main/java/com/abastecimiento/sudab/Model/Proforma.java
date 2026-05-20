package com.abastecimiento.sudab.Model;


import java.time.LocalDate;
import java.util.List;

import com.abastecimiento.sudab.Model.requerimiento.Requerimiento;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Table(name = "proforma")
public class Proforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProforma;

    @Column(unique = true, nullable = false)
    private String codigo; // Ej: PROF-DIST-LIMA-992

    @Column(name = "fecha_recepcion", nullable = false)
    private LocalDate fechaRecepcion;

    @Column(name="precio_total", nullable = false)
    private Double precioTotal;


    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
    

    @ManyToOne
    @JoinColumn(name = "id_requerimiento", nullable = false)
    private Requerimiento requerimiento;

    @ManyToOne
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;


    @OneToMany(mappedBy="proforma",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProformaDetalle> productos;



}
