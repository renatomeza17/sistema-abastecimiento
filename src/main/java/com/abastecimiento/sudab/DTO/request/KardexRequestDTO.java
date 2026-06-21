package com.abastecimiento.sudab.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KardexRequestDTO{

    // El ID del producto seleccionado del catálogo maestro que aún no tiene Kárdex
    private Long idProducto; 
    
    // Parámetro clave exigido por la HU11 (Definición de stock mínimo)
    private Integer stockMinimo;
    
    // Ubicación física en el almacén de la UNI (Ej: "Estante C-A2", "Piso 1 - Zona Líquidos")
    private String ubicacionAlmacen;
    
    // Características iniciales del bien o ficha técnica inicial (HU11)
    private String caracteristicas;

}
