package co.edu.unipiloto.edu.proyectoVotos.controller;

import co.edu.unipiloto.edu.proyectoVotos.dao.*;
import co.edu.unipiloto.edu.proyectoVotos.model.*;
import co.edu.unipiloto.edu.proyectoVotos.tareas.*;
import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author tomas
 */
@RestController
@RequestMapping("/proyecto")
public class ProyectoController {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VotoRepository votoRepository;

    @Autowired
    private TipoRepository tipoRepository;

    @Autowired
    private ProcesoRepository procesoRepository;

    @PostMapping("/adicionarProyecto/{usuarioId}")
    public ResponseEntity<?> adicionarProyecto(@PathVariable Integer usuarioId, @RequestBody Proyecto proyecto) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String rolDescripcion = usuario.getRol().getDescripcion();

        if (!rolDescripcion.equalsIgnoreCase("planeador")) {
            // Mensaje personalizado cuando el rol no es "planeador"
            String mensaje = "Usted no es un planeador, usted es " + rolDescripcion;
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(mensaje));
        }

        Procesodevotacion proceso = procesoRepository.findByIdentificador(proyecto.getProcesodevotacion().getIdentificador())
                .orElseThrow(() -> new RuntimeException("Proceso de votación no encontrado"));

        Tiposdeproyecto tipo = tipoRepository.findByIdentificador(proyecto.getTipodeproyecto().getIdentificador())
                .orElseThrow(() -> new RuntimeException("Tipo de proyecto no encontrado"));

        proyecto.setProcesodevotacion(proceso);
        proyecto.setTipodeproyecto(tipo);

        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);
        return ResponseEntity.ok(proyectoGuardado);
    }

    @PutMapping("/modificarProyecto/{id}/{usuarioId}")
    public ResponseEntity<Proyecto> modificarProyecto(@PathVariable Integer id, @PathVariable Integer usuarioId, @RequestBody Proyecto proyectoDetails) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String rolDescripcion = usuario.getRol().getDescripcion();

        if (!rolDescripcion.equalsIgnoreCase("planeador")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        proyecto.setNombreProyecto(proyectoDetails.getNombreProyecto());
        proyecto.setAlcance(proyectoDetails.getAlcance());
        // Otros setters

        return ResponseEntity.ok(proyectoRepository.save(proyecto));
    }

    @DeleteMapping("/eliminarProyecto/{id}/{usuarioId}")
    public ResponseEntity<String> eliminarProyecto(@PathVariable Integer id, @PathVariable Integer usuarioId) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String rolDescripcion = usuario.getRol().getDescripcion();

        if (!rolDescripcion.equalsIgnoreCase("planeador")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo los planeadores pueden eliminar proyectos.");
        }

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // Validar que el usuario sea el creador del proyecto o el planeador
        if (!proyecto.getIdentificador().equals(usuario.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para eliminar este proyecto.");
        }

        proyectoRepository.delete(proyecto);
        return ResponseEntity.ok("Proyecto eliminado exitosamente.");
    }

    @GetMapping("/consultarProyecto")
    public List<Proyecto> consultarProyecto() {
        return proyectoRepository.findAll();
    }
}
