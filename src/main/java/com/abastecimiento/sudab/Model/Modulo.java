package com.abastecimiento.sudab.Model;

import jakarta.persistence.*;
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

    @Column(nullable = false, unique = true)
    private String activo;
}