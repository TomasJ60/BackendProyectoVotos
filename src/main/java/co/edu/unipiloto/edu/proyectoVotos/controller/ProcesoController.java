package co.edu.unipiloto.edu.proyectoVotos.controller;

import co.edu.unipiloto.edu.proyectoVotos.dao.*;
import co.edu.unipiloto.edu.proyectoVotos.model.*;
import co.edu.unipiloto.edu.proyectoVotos.tareas.ErrorResponse;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
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
    
    @Autowired
    private UserRepository userRepository;
    
    
    private ResponseEntity<?> verificarRolPlaneador(User usuario) {
        String rolDescripcion = usuario.getRol().getDescripcion();
        if (!rolDescripcion.equalsIgnoreCase("planeador")) {
            String mensaje = "Usted no es un planeador, usted es " + rolDescripcion;
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensaje);
        }
        return null;
    }
    

    @PostMapping("/adicionarProceso/{usuarioId}")
    public ResponseEntity<?> adicionarProceso(@RequestBody Procesodevotacion proceso, @PathVariable Integer usuarioId) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        ResponseEntity<?> respuestaRol = verificarRolPlaneador(usuario);
        if (respuestaRol != null) {
            return respuestaRol;
        }

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

    @PutMapping("/modificarProceso/{id}/{usuarioId}")
    public ResponseEntity<?> modificarProceso(@PathVariable Integer id, @RequestBody Procesodevotacion ProcesoDetails, @PathVariable Integer usuarioId) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ResponseEntity<?> respuestaRol = verificarRolPlaneador(usuario);
        if (respuestaRol != null) {
            return respuestaRol;
        }

        Procesodevotacion procesodevotacion = procesoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proceso de votación no encontrado"));

        procesodevotacion.setFechadeinicio(ProcesoDetails.getFechadeinicio());
        procesodevotacion.setFechadecierre(ProcesoDetails.getFechadecierre());
        procesodevotacion.setEstado(ProcesoDetails.getEstado());
        
        procesoRepository.save(procesodevotacion);
        return ResponseEntity.ok(procesodevotacion);
    }

    @DeleteMapping("/eliminarProceso/{id}/{usuarioId}")
    public ResponseEntity<?> eliminarProceso(@PathVariable Integer id, @PathVariable Integer usuarioId) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ResponseEntity<?> respuestaRol = verificarRolPlaneador(usuario);
        if (respuestaRol != null) {
            return respuestaRol;
        }

        procesoRepository.deleteById(id);
        return ResponseEntity.ok("Proceso de votación eliminado exitosamente");
    }

    @GetMapping("/verificarVotacion/{idproyecto}")  // Usamos @PathVariable para capturar el id del proyecto
    public ResponseEntity<?> verificarVotacion(@PathVariable Integer idproyecto) {
        // Buscar el proyecto por id
        Proyecto proyecto = proyectoRepository.findById(idproyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        
        // Obtener el proceso de votación asociado al proyecto
        Procesodevotacion proceso = proyecto.getProcesodevotacion();

        // Actualizar el estado del proceso de votación
        proceso.checkEstado();

        // Guardar los cambios en el proceso de votación si el estado ha cambiado
        procesoRepository.save(proceso);

        // Obtener la fecha y hora actual
        LocalDateTime fechaActual = LocalDateTime.now();

        // Verificar el estado del proceso de votación
        if (proceso.getEstado().equals("activo")) {
            if (fechaActual.isBefore(proceso.getFechadecierre())) {
                return ResponseEntity.ok("Puedes votar");
            } else {
                return ResponseEntity.badRequest().body("Las votaciones terminaron y no se puede votar");
            }
        } else if (proceso.getEstado().equals("cerrado")) {
            return ResponseEntity.badRequest().body("Las votaciones terminaron y no se puede votar, se cerró: " + proceso.getFechadecierre());
        } else if (proceso.getEstado().equals("definido")) {
            return ResponseEntity.ok("El proceso de votación del proyecto aún está en espera. Por favor espera hasta " + proceso.getFechadeinicio());
        } else {
            return ResponseEntity.badRequest().body("Estado del proceso de votación no válido");
        }
    }

}
