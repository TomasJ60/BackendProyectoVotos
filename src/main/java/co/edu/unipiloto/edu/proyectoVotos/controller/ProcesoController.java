package co.edu.unipiloto.edu.proyectoVotos.controller;

import co.edu.unipiloto.edu.proyectoVotos.dao.*;
import co.edu.unipiloto.edu.proyectoVotos.model.*;
import java.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author tomas
 */
@RestController
@RequestMapping("/proceso")
public class ProcesoController {
    
    @Autowired
    private ProcesoRepository procesoRepository;
    
    @PostMapping("/adicionarProceso")
    public ResponseEntity<?> adicionarProceso(@RequestBody Procesodevotacion proceso) {
        if (procesoRepository.existsByIdentificador(proceso.getIdentificador())) {
            return ResponseEntity.badRequest().body("Ese proceso ya está en uso");
        }
        
        Procesodevotacion nuevoProceso = procesoRepository.save(proceso);
        return ResponseEntity.ok(nuevoProceso);
    }
    
    @GetMapping("/consultarProceso")
    public List<Procesodevotacion> consultarProceso() {
        return procesoRepository.findAll();
    }
    
    @PutMapping("/modificarProceso/{id}")
    public Procesodevotacion modificarProceso(@PathVariable Integer id, @RequestBody Procesodevotacion ProcesoDetails) {
        Procesodevotacion procesodevotacion = procesoRepository.findById(id).orElseThrow(() -> new RuntimeException("Proceso de votacion no encontrado"));
        procesodevotacion.setIdentificador(ProcesoDetails.getIdentificador());
        procesodevotacion.setFechadeinicio(ProcesoDetails.getFechadeinicio());
        procesodevotacion.setFechadecierre(ProcesoDetails.getFechadecierre());
        procesodevotacion.setEstado(ProcesoDetails.getEstado());
        return procesoRepository.save(procesodevotacion);
    }
    
    @DeleteMapping("/eliminarProceso/{id}")
    public String eliminarProceso(@PathVariable Integer id) {
        procesoRepository.deleteById(id);
        return "Proceso de votacion eliminado exitosamente";
    }
}
