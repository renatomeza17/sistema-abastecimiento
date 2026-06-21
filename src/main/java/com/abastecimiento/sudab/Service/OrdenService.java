package com.abastecimiento.sudab.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

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
    private final KardexService kardexService;


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
       
        nuevaOC.setDescripcion("Orden de compra");


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
        // nuevaOC.setEstado ("SELECCIONADO");

        nuevaOC.setFechaEntrega(LocalDate.parse(ordenrequest.getFechaEntrega())); // Si el DTO manda String, lo pasas a LocalDate
        nuevaOC.setLugarEntrega(ordenrequest.getLugarEntrega());
        nuevaOC.setObservaciones(ordenrequest.getObservaciones());
        nuevaOC.setFormaPago(ordenrequest.getFormaPago());
        nuevaOC.setPlazoEntrega(ordenrequest.getPlazoEntrega());
        nuevaOC.setGarantia(ordenrequest.getGarantia());


        proforma.setEstado("SELECCIONADA");

        OrdenCompra ocGuardad=ordenCompraRepository.save(nuevaOC);


        return convertirAConvertirDTO(ocGuardad);
        
      }



 
    public List<OrdenResponseDTO> listarOrdenService() {
        List<OrdenCompra> ordenes= ordenCompraRepository.findAll();
        return ordenes.stream().map(this::convertirAConvertirDTO).toList();
    }


    @Transactional(readOnly = true)
    public OrdenResponseDTO consultarOrdenService(Long id) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada."));
        return convertirAConvertirDTO(orden);
    }


    public OrdenResponseDTO enviarOrdenService(Long id) {
        // 1. Buscas la orden que aprobó el director administrativo
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada."));

        // 2. Validación: Que esté aprobada para poder mandarla
        if (!"APROBADA".equals(orden.getEstado())) {
            throw new IllegalStateException("La orden debe estar APROBADA para enviarse.");
        }

        // 3. EL CAMBIO CLAVE: Cambias el estado a ENVIADA. 
        // Al guardarse en la BD, automáticamente el proveedor ya la puede ver en su usuario.
        orden.setEstado("ENVIADA");
        
        OrdenCompra ordenEnviada = ordenCompraRepository.save(orden);
        return convertirAConvertirDTO(ordenEnviada);
    }   





    // public String aprobarOrdenService(Long id) {

    //     OrdenCompra orden=ordenCompraRepository.findById(id).orElseThrow(() -> new RuntimeException("Orden no encontrada."));


    //     if(!"ENVIADA".equals(orden.getEstado())){
    //         throw new IllegalStateException("Solo se pueden aprobar órdenes en estado ENVIADA.");
    //     }

    //     orden.setEstado("APROBADA");
    //     ordenCompraRepository.save(orden);

    //     return "Orden aprobada exitosamente.";
        

    // }


    @Transactional
    public OrdenResponseDTO autorizarYFirmarOrden(Long idOrden, String usernameDirector) {
        // 1. Buscar la orden
        OrdenCompra orden = ordenCompraRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada: " + idOrden));

        // 2. Validar estado válido para firma
        if (!"PENDIENTE".equals(orden.getEstado())) {
            throw new IllegalStateException("La orden no se encuentra en estado PENDIENTE_AUTORIZACION");
        }

        // 3. Modificar datos de control de flujo
        orden.setEstado("APROBADA");
        orden.setAutorizadoPor(usernameDirector);
        orden.setFechaAutorizacion(LocalDateTime.now());

        // 4. Simulación Avanzada de Sello / Firma Digital Criptográfica
        try {
            String datosAFirmar = orden.getCodigo() + "|" + orden.getFechaAutorizacion() + "|" + usernameDirector;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(datosAFirmar.getBytes(StandardCharsets.UTF_8));
            String firmaCalculada = Base64.getEncoder().encodeToString(hashBytes);
            
            orden.setFirmaDigitalHash("SUDAB-SIG-" + firmaCalculada);
        } catch (Exception e) {
            orden.setFirmaDigitalHash("SUDAB-SIG-FALLBACK-HASH-" + orden.getIdOrden());
        }

        // 5. Guardar cambios en Neon DB
        OrdenCompra ordenGuardada = ordenCompraRepository.save(orden);

        // 6. Mapear y retornar tu DTO de respuesta habitual
        return convertirAConvertirDTO(ordenGuardada);
    }


    public String archivarOrdenService(Long id){
        OrdenCompra orden=ordenCompraRepository.findById(id).orElseThrow(() -> new RuntimeException("Orden no encontrada."));

        if(!"APROBADA".equals(orden.getEstado())){
            throw new IllegalStateException("Solo se pueden archivar órdenes en estado APROBADA.");
        }


        orden.setEstado("ARCHIVADA");
        ordenCompraRepository.save(orden);

        return "Orden archivada exitosamente.";


    }


    
    @Transactional
    public String cancelarOrdenService(Long id) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada."));

        // Solo se puede cancelar si no ha sido enviada al proveedor
        if ("ENVIADA".equals(orden.getEstado()) || "ARCHIVADA".equals(orden.getEstado())) {
            throw new IllegalStateException("No se puede cancelar una orden que ya fue despachada o archivada.");
        }

        orden.setEstado("CANCELADA");
        
        // Liberamos la proforma asociada para que pueda volver a cotizarse si es necesario
        if (orden.getProforma() != null) {
            Proforma proforma = orden.getProforma();
            proforma.setEstado("PENDIENTE"); // O el estado inicial de tu flujo de proformas
            proformaRepository.save(proforma);
        }

        ordenCompraRepository.save(orden);
        return "Orden cancelada exitosamente y proforma liberada.";
    }


    public List<OrdenResponseDTO> listarOrdenesPorProveedorService(Long idProveedor) {
    // Suponiendo que tu entidad Orden tiene una relación con Proveedor o guarda su ID:
    return ordenCompraRepository.findByProveedorIdProveedor(idProveedor).stream()
            .map(this::convertirAConvertirDTO) // Usa tu método existente de conversión a DTO
            .collect(Collectors.toList());
    }   




    private OrdenResponseDTO convertirAConvertirDTO(OrdenCompra oc) {
        OrdenResponseDTO dto = new OrdenResponseDTO();
        dto.setIdOrden(oc.getIdOrden());
        dto.setCodigo(oc.getCodigo());
        dto.setFechaCreacion(oc.getFechaCreacion());
        dto.setMontoTotal(oc.getMontoTotal());
        dto.setEstado(oc.getEstado());
        
        if (oc.getProveedor() != null) {
            dto.setNombreProveedor(oc.getProveedor().getRazonSocial());
            dto.setRucProveedor(oc.getProveedor().getRuc());
        }
        else{
            dto.setNombreProveedor("Proveedor no asignado");
            dto.setRucProveedor("0000000");
        }
        
        if (oc.getProforma() != null && oc.getProforma().getRequerimiento() != null) {
            dto.setCodigoRequerimiento(oc.getProforma().getRequerimiento().getCodigo());
        }

        
        
        if (oc.getDetalles() != null) {
            List<OrdenDetalleDTO> detallesDTO = oc.getDetalles().stream().map(detalle -> {
                OrdenDetalleDTO dDto = new OrdenDetalleDTO();
                dDto.setId(detalle.getId());
                if (detalle.getProducto() != null) {
                    dDto.setProductoId(detalle.getProducto().getIdProducto());
                    dDto.setNombreProducto(detalle.getProducto().getNombre());
                }
                dDto.setCantidad(detalle.getCantidad());
                dDto.setPrecioUnitario(detalle.getPrecioUnitario());
                
                // Calculamos dinámicamente el subtotal en caso de que detalle.getSubtotal() no esté mapeado
                dDto.setSubtotal(detalle.getCantidad() * detalle.getPrecioUnitario());
                return dDto;
            }).toList();
            dto.setDetalles(detallesDTO);
        }
        
        return dto;
    }
    




    @Transactional
    public String procesarRecepcionConforme(Long idOrden) {
        // 1. El almacenero da el clic al constatar que las cajas llegaron completas.
        OrdenCompra orden = ordenCompraRepository.findById(idOrden).orElseThrow();
        
        // 2. Cambiamos el estado de la OC porque ya entró físicamente a las instalaciones
        orden.setEstado("PROCESADA_ALMACEN"); 
        ordenCompraRepository.save(orden);
        
        // 3. RECIÉN AQUÍ, con las cosas completas en la mesa, el sistema actualiza el stock automáticamente
        for (OrdenCompraDetalle detalle : orden.getDetalles()) {
            kardexService.registrarMovimiento(
                detalle.getProducto().getIdProducto(), 
                detalle.getCantidad(), 
                "ENTRADA", 
                orden.getCodigo(), 
                "Ingreso físico verificado por Almacén"
            );
        }
        return "Bienes ingresados al almacén con éxito.";
    }



}
