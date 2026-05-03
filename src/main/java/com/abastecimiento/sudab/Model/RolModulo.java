package com.abastecimiento.sudab.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "rol_modulo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolModulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRolMod;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "id_mod", nullable = false) 
    private Modulo modulo;

    // Estos campos garantizan los "Permisos" que pide la tarea
    private String puedeCrear; // Puedes usar "S"/"N" para seguir tu estilo
    private String puedeLeer;
    private String puedeActualizar;
    private String puedeEliminar;
}