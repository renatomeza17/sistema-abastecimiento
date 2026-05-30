package com.abastecimiento.sudab.Controller;


import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abastecimiento.sudab.DTO.request.OrdenRequestDTO;
import com.abastecimiento.sudab.DTO.response.OrdenResponseDTO;
import com.abastecimiento.sudab.Service.OrdenService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;





@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrdenController {

    private final OrdenService ordenService;

    
    @PostMapping
    public ResponseEntity<OrdenResponseDTO> crearOrden(@RequestBody OrdenRequestDTO ordenRequest) {
        OrdenResponseDTO response = ordenService.generarOrden(ordenRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED); // Devuelve HTTP 201 Created
    }

    
    @GetMapping
    public ResponseEntity<List<OrdenResponseDTO>> listarOrdenes() {
        List<OrdenResponseDTO> lista = ordenService.listarOrdenService();
        return ResponseEntity.ok(lista); // Devuelve HTTP 200 OK
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponseDTO> consultarOrden(@PathVariable Long id) {
        OrdenResponseDTO response = ordenService.consultarOrdenService(id);
        return ResponseEntity.ok(response);
    }

    
    @PostMapping("/{id}/enviar")
    public ResponseEntity<OrdenResponseDTO> enviarOrden(@PathVariable Long id) {
        OrdenResponseDTO response = ordenService.enviarOrdenService(id);
        return ResponseEntity.ok(response);
    }

    // 5. APROBAR ORDEN (PUT /api/v1/ordenes/{id}/aprobar)
    // @PutMapping("/{id}/aprobar")
    // public ResponseEntity<String> aprobarOrden(@PathVariable Long id) {
    //     String mensaje = ordenService.aprobarOrdenService(id);
    //     return ResponseEntity.ok(mensaje);
    // }


    
    @PutMapping("/aprobarOrden")
    public ResponseEntity<OrdenResponseDTO> aprobarOrden(@RequestParam Long id, Principal principal) {
        // El objeto Principal jala automáticamente el usuario autenticado desde el JwtFilter
        String usernameDirector = (principal != null) ? principal.getName() : "DIRECTOR ADMINISTRATIVO";
        
        OrdenResponseDTO response = ordenService.autorizarYFirmarOrden(id, usernameDirector);
        return ResponseEntity.ok(response);
    }




    // 6. ARCHIVAR ORDEN (PUT /api/v1/ordenes/{id}/archivar)
    @PutMapping("/{id}/archivar")
    public ResponseEntity<String> archivarOrden(@PathVariable Long id) {
        String mensaje = ordenService.archivarOrdenService(id);
        return ResponseEntity.ok(mensaje);
    }

    
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<String> cancelarOrden(@PathVariable Long id) {
        String mensaje = ordenService.cancelarOrdenService(id);
        return ResponseEntity.ok(mensaje);
    }
    

    @GetMapping("/listarPorProveedor")
    public List<OrdenResponseDTO> listarPorProveedor(@RequestParam Long idProveedor) {
        return ordenService.listarOrdenesPorProveedorService(idProveedor);
    }







    //HECHO A MANO

    // @PostMapping
    // public OrdenResponseDTO crearOrden(@Requestbody OrdenRequestDTO ordenRequest) {
    //     return ordenService.generarOrden(ordenRequest);  
    // }



    // @GetMapping
    // public List<OrdenResponseDTO> listarOrden() {
    //     return ordenService.listarOrdenService();
    // }


    // @GetMapping("/consultarOrden")
    // public OrdenResponseDTO consultarOrden(@RequestParam Long id) {
    //     return ordenService.consultarOrdenService(id);
    // }


    // @PostMapping("/enviarOrden")
    // public OrdenResponseDTO enviarOrden(@RequestParam Long id) {
    //     return ordenService.enviarOrdenService(id);
    // }


    // @PutMapping("/aprobarOrden")
    // public String aprobarOrden(@RequestParam Long id) {
    //     return ordenService.aprobarOrdenService(id);
    // }


    // @PutMapping("/archivarOrden")
    // public String archivarOrden(@RequestParam Long id) {
    //     return ordenService.archivarOrdenService(id);
    // }

   
    // @DeleteMapping("/cancelarOrden")
    // public String cancelarOrden(@RequestParam Long id) {
    //     return ordenService.cancelarOrdenService(id);
    // }
    
    
    





    

    






}
