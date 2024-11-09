package co.edu.unipiloto.edu.proyectoVotos.controller;

import co.edu.unipiloto.edu.proyectoVotos.dao.*;
import co.edu.unipiloto.edu.proyectoVotos.model.*;
import java.util.*;
import java.util.stream.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author tomas
 */
public class DecisorController {
    
    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private VotoRepository votoRepository;

//    @Autowired
//    private TipoRepository tipoRepository;

//    @Autowired
//    private ProcesoRepository procesoRepository;
    
    @GetMapping("/filtrarProyectosPorLocalidad")
    public List<Proyecto> filtrarProyectosPorLocalidad(@RequestParam String localidad) {
        return proyectoRepository.findByLocalidad(localidad);
    }

    //"Decisor" - Ver el número total de votos por localidad
    // Método para el Decisor: Obtener total de votos por localidad
    @GetMapping("/totalVotosPorLocalidad")
    public ResponseEntity<?> totalVotosPorLocalidad(@RequestParam Integer decisorId) {
        User decisor = userRepository.findById(decisorId)
                .orElseThrow(() -> new RuntimeException("Decisor no encontrado"));

        if (!decisor.getRol().getDescripcion().equalsIgnoreCase("decisor")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo los decisores pueden ver los resultados.");
        }

        // Lógica para calcular total de votos por localidad
        Map<String, Integer> votosPorLocalidad = new HashMap<>();
        for (Proyecto proyecto : proyectoRepository.findAll()) {
            votosPorLocalidad.put(proyecto.getLocalidad(),
                    votosPorLocalidad.getOrDefault(proyecto.getLocalidad(), 0) + proyecto.getVotosSi());
        }

        return ResponseEntity.ok(votosPorLocalidad);
    }

    //Rol decisor
    // Método para el Decisor: Consultar votos en proyectos de una localidad específica
    @GetMapping("/consultarVotos")
    public ResponseEntity<?> consultarVotos(@RequestParam Integer decisorId, @RequestParam String localidad) {
        User decisor = userRepository.findById(decisorId)
                .orElseThrow(() -> new RuntimeException("Decisor no encontrado"));
        String rolDescripcion = decisor.getRol().getDescripcion();

        if (!proyectoRepository.tienePermiso(rolDescripcion, "consultarVotos")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Tu rol es " + rolDescripcion + ". Solo los decisores pueden consultar los votos.");
        }

        List<Proyecto> proyectos = proyectoRepository.findByLocalidad(localidad).stream()
                .sorted((p1, p2) -> Integer.compare(p2.getVotosSi(), p1.getVotosSi()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(proyectos);
    }
    
    
}
