package com.abastecimiento.sudab.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abastecimiento.sudab.DTO.request.OrdenRequestDTO;
import com.abastecimiento.sudab.DTO.response.OrdenDetalleDTO;
import com.abastecimiento.sudab.DTO.response.OrdenResponseDTO;
import com.abastecimiento.sudab.Model.compra.OrdenCompra;
import com.abastecimiento.sudab.Model.compra.OrdenCompraDetalle;
import com.abastecimiento.sudab.Model.requerimiento.Proforma;
import com.abastecimiento.sudab.Model.requerimiento.ProformaDetalle;
import com.abastecimiento.sudab.Repository.OrdenCompraRepository;
import com.abastecimiento.sudab.Repository.ProformaRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdenService {

    private final ProformaRepository proformaRepository;
    private final OrdenCompraRepository ordenCompraRepository;


    @Transactional
    public OrdenResponseDTO generarOrden(OrdenRequestDTO ordenrequest){

        Long idProforma=ordenrequest.getIdProforma();

        //Buscar la proforma en la BD

        Proforma proforma= proformaRepository.findById(idProforma).orElseThrow(() -> new RuntimeException("Proforma no encontrada."));

        // Validación de seguridad para evitar duplicaciones accidentales
        if("SELECCIONADA".equals(proforma.getEstado())){
            throw new IllegalStateException("La proforma ya ha sido seleccionada para una orden.");
        }



        OrdenCompra nuevaOC= new OrdenCompra();


        nuevaOC.setCodigo("OC-"+LocalDate.now().getYear()+"-"+ (System.currentTimeMillis() % 100000));
        nuevaOC.setFechaCreacion(LocalDate.now());


        nuevaOC.setEstado("PENDIENTE");


        nuevaOC.setProforma(proforma);
        nuevaOC.setProveedor(proforma.getProveedor());
        
        
        List<OrdenCompraDetalle> productos= new ArrayList<>();
        Double monto=0.0;

        for(ProformaDetalle item: proforma.getProductos()){
            OrdenCompraDetalle detallesOC= new OrdenCompraDetalle();

            detallesOC.setProducto(item.getProducto());
            detallesOC.setCantidad(item.getCantidad());
            detallesOC.setPrecioUnitario(item.getPrecioUnitario());

            detallesOC.setOrdenCompra(nuevaOC);

            
            monto+=item.getPrecioUnitario()*item.getCantidad(); 
            productos.add(detallesOC);
        }

        nuevaOC.setMontoTotal(monto);
        nuevaOC.setDetalles(productos);
        nuevaOC.setEstado ("SELECCIONADO");



        OrdenCompra ocGuardad=ordenCompraRepository.save(nuevaOC);


        return convertirAConvertirDTO(ocGuardad);
        
        
        //Obtener datos de la proforma para la orden
        // profor
        // List<String>




    }




    private OrdenResponseDTO convertirAConvertirDTO(OrdenCompra oc) {
        OrdenResponseDTO dto = new OrdenResponseDTO();
        dto.setIdOrden(oc.getIdOrden());
        dto.setCodigo(oc.getCodigo());
        dto.setFechaCreacion(oc.getFechaCreacion());
        dto.setMontoTotal(oc.getMontoTotal());
        dto.setEstado(oc.getEstado());
        
        // Desnormalización controlada para la vista del Frontend
        dto.setNombreProveedor(oc.getProveedor().getRazonSocial());
        dto.setRucProveedor(oc.getProveedor().getRuc());
        
        // Salto relacional de trazabilidad: OC -> Proforma -> Requerimiento
        dto.setCodigoRequerimiento(oc.getProforma().getRequerimiento().getCodigo());
        
        // Mapeo de la sublista de ítems (Detalles)
        List<OrdenDetalleDTO> detallesDTO = oc.getDetalles().stream().map(detalle -> {
            OrdenDetalleDTO dDto = new OrdenDetalleDTO();
            dDto.setId(detalle.getId());
            dDto.setProductoId(detalle.getProducto().getIdProducto());
            dDto.setNombreProducto(detalle.getProducto().getNombre());
            dDto.setCantidad(detalle.getCantidad());
            dDto.setPrecioUnitario(detalle.getPrecioUnitario());
            dDto.setSubtotal(detalle.getSubtotal());
            return dDto;
        }).toList();
        
        dto.setDetalles(detallesDTO);
        return dto;
    }

    




}
