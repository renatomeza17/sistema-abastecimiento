package com.abastecimiento.sudab.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Data
@Table(name = "modulo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Modulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_mod")
    private Integer idMod;

    @Column(nullable = false, unique = true)
    private String descripcion;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(nullable = false)
    private String activo;
}