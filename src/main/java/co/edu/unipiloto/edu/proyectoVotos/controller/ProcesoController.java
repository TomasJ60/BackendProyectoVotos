package co.edu.unipiloto.edu.proyectoVotos.controller;

import co.edu.unipiloto.edu.proyectoVotos.dao.*;
import co.edu.unipiloto.edu.proyectoVotos.model.*;
import java.time.LocalDateTime;
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
    
    @Autowired
    private ProyectoRepository proyectoRepository;
    
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
    
    @GetMapping("/verificarVotacion")
    public ResponseEntity<?> verificarVotacion(@RequestParam String nombreProyecto) {
        Proyecto proyecto = proyectoRepository.findByNombreProyecto(nombreProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        
        Procesodevotacion proceso = proyecto.getProcesodevotacion();
        proceso.checkEstado();
        
        // Guardar cambios en caso de que el estado haya cambiado
        procesoRepository.save(proceso);

        LocalDateTime fechaActual = LocalDateTime.now();
        
        if (proceso.getEstado().equals("activo")) {
            if (fechaActual.isBefore(proceso.getFechadecierre())) {
                return ResponseEntity.ok("Puedes votar");
            } else {
                return ResponseEntity.badRequest().body("Las votaciones terminaron y no se puede votar");
            }
        } else if (proceso.getEstado().equals("cerrado")) {
            return ResponseEntity.badRequest().body("Las votaciones terminaron y no se puede votar, se cerro: " + proceso.getFechadecierre());
        } else if (proceso.getEstado().equals("definido")) {
            return ResponseEntity.ok("El proceso de votación del proyecto aún está en espera. Por favor espera hasta " + proceso.getFechadeinicio());
        } else {
            return ResponseEntity.badRequest().body("Estado del proceso de votación no válido");
        }
    }
    
    
}
