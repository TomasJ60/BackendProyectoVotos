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
@RestController
@RequestMapping("/decisor")
public class DecisorController {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VotoRepository votoRepository;

    private boolean esDecisor(User user) {
        return user.getRol().getDescripcion().equalsIgnoreCase("decisor");
    }

    // Método para verificar los proyectos ganadores por localidad
    @GetMapping("/proyectoGanadorPorLocalidad/{idusuario}")
    public ResponseEntity<?> proyectoGanadorPorLocalidad(@PathVariable("idusuario") Integer decisorId) {
        User decisor = userRepository.findById(decisorId)
                .orElseThrow(() -> new RuntimeException("Decisor no encontrado"));

        if (!esDecisor(decisor)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usted no es un decisor, su rol es " + decisor.getRol().getDescripcion() + ".");
        }

        Map<String, Proyecto> ganadorPorLocalidad = new HashMap<>();
        for (Proyecto proyecto : proyectoRepository.findAll()) {
            // Verifica si el estado del proyecto es 'cerrado'
            if (!proyecto.getEstadoVotacion().equalsIgnoreCase("cerrado")) {
                continue;
            }

            if (!ganadorPorLocalidad.containsKey(proyecto.getLocalidad())
                    || proyecto.getVotosSi() > ganadorPorLocalidad.get(proyecto.getLocalidad()).getVotosSi()) {
                ganadorPorLocalidad.put(proyecto.getLocalidad(), proyecto);
            }
        }

        if (ganadorPorLocalidad.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No hay proyectos cerrados para mostrar.");
        }

        return ResponseEntity.ok(ganadorPorLocalidad);
    }

    // Método para obtener el total de votos por localidad
    @GetMapping("/totalVotosPorLocalidad/{idusuario}")
    public ResponseEntity<?> totalVotosPorLocalidad(@PathVariable("idusuario") Integer decisorId) {
        User decisor = userRepository.findById(decisorId)
                .orElseThrow(() -> new RuntimeException("Decisor no encontrado"));

        if (!esDecisor(decisor)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usted no es un decisor, su rol es " + decisor.getRol().getDescripcion() + ".");
        }

        Map<String, Integer> votosPorLocalidad = new HashMap<>();
        for (Proyecto proyecto : proyectoRepository.findAll()) {
            if (!proyecto.getEstadoVotacion().equalsIgnoreCase("cerrado")) {
                continue;  // Solo contar proyectos cerrados
            }
            votosPorLocalidad.put(proyecto.getLocalidad(),
                    votosPorLocalidad.getOrDefault(proyecto.getLocalidad(), 0)
                    + proyecto.getVotosSi() + proyecto.getVotosNo() + proyecto.getVotosBlanco());
        }

        return ResponseEntity.ok(votosPorLocalidad);
    }

    // Método para obtener los votos por proyecto y localidad
    @GetMapping("/votosPorProyectoLocalidad/{idusuario}/{idproyecto}/{localidad}")
    public ResponseEntity<?> votosPorProyectoLocalidad(
            @PathVariable("idusuario") Integer decisorId,
            @PathVariable("idproyecto") Integer proyectoId,
            @PathVariable("localidad") String localidad) {

        User decisor = userRepository.findById(decisorId)
                .orElseThrow(() -> new RuntimeException("Decisor no encontrado"));

        if (!esDecisor(decisor)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usted no es un decisor, su rol es " + decisor.getRol().getDescripcion() + ".");
        }

        Proyecto proyecto = proyectoRepository.findByIdAndLocalidad(proyectoId, localidad)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado en la localidad especificada."));

        // Verifica si el estado de votación es "cerrado"
        if (!proyecto.getEstadoVotacion().equalsIgnoreCase("cerrado")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Los proyectos aún continúan en espera o estado activado, por favor espere.");
        }

        Map<String, Integer> votos = new HashMap<>();
        votos.put("SI", proyecto.getVotosSi());
        votos.put("NO", proyecto.getVotosNo());
        votos.put("BLANCO", proyecto.getVotosBlanco());

        Map<String, Map<String, Integer>> votosPorProyecto = new HashMap<>();
        votosPorProyecto.put(proyecto.getNombreProyecto(), votos);

        return ResponseEntity.ok(votosPorProyecto);
    }

    @GetMapping("/discriminacionVotosPorGenero/{idusuario}/{genero}")
    public ResponseEntity<?> discriminacionVotosPorGenero(@PathVariable("idusuario") Integer decisorId, @PathVariable("genero") String genero) {
        User decisor = userRepository.findById(decisorId)
                .orElseThrow(() -> new RuntimeException("Decisor no encontrado"));

        if (!esDecisor(decisor)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usted no es un decisor, su rol es " + decisor.getRol().getDescripcion() + ".");
        }

        // Filtra los votos por el género especificado, solo si el proyecto está cerrado
        Map<String, Long> votosPorGenero = votoRepository.findAll().stream()
                .filter(voto -> voto.getProyecto().getEstadoVotacion().equalsIgnoreCase("cerrado")
                && voto.getCiudadano().getGenero().equalsIgnoreCase(genero))
                .collect(Collectors.groupingBy(
                        voto -> voto.getCiudadano().getGenero(),
                        Collectors.counting()
                ));

        if (votosPorGenero.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Los proyectos aun continúan en espera o estado activado, por favor espere.");
        }

        return ResponseEntity.ok(votosPorGenero);
    }

}
