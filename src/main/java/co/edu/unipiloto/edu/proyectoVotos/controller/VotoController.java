package co.edu.unipiloto.edu.proyectoVotos.controller;

import co.edu.unipiloto.edu.proyectoVotos.dao.*;
import co.edu.unipiloto.edu.proyectoVotos.model.*;
import static co.edu.unipiloto.edu.proyectoVotos.model.VotoEnum.*;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author tomas
 */
@RestController
@RequestMapping("/ciudadano")
public class VotoController {

    @Autowired
    private ProyectoRepository proyectoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VotoRepository votoRepository;

    @Autowired
    private ProcesoRepository procesoRepository;

    private ResponseEntity<?> verificarEstadoProceso(Procesodevotacion proceso) {
        LocalDateTime fechaActual = LocalDateTime.now();

        if (proceso.getEstado().equals("activo")) {
            if (fechaActual.isAfter(proceso.getFechadecierre())) {
                return ResponseEntity.badRequest().body("Las votaciones terminaron y no se puede votar");
            }
        } else if (proceso.getEstado().equals("cerrado")) {
            return ResponseEntity.badRequest().body("Las votaciones terminaron y no se puede votar, se cerró: " + proceso.getFechadecierre());
        } else if (proceso.getEstado().equals("definido")) {
            return ResponseEntity.ok("El proceso de votación del proyecto aún está en espera. Por favor espera hasta " + proceso.getFechadeinicio());
        } else {
            return ResponseEntity.badRequest().body("Estado del proceso de votación no válido");
        }

        return ResponseEntity.ok("El proceso está activo");
    }

    private void actualizarContadoresVoto(Proyecto proyecto, Voto voto) {
        switch (voto.getVoto()) {
            case SI:
                proyecto.setVotosSi(proyecto.getVotosSi() + 1);
                break;
            case NO:
                proyecto.setVotosNo(proyecto.getVotosNo() + 1);
                break;
            case BLANCO:
                proyecto.setVotosBlanco(proyecto.getVotosBlanco() + 1);
                break;
        }
        proyectoRepository.save(proyecto);
    }

    private void restarVotoAnterior(Proyecto proyecto, Voto voto) {
        switch (voto.getVoto()) {
            case SI:
                proyecto.setVotosSi(proyecto.getVotosSi() - 1);
                break;
            case NO:
                proyecto.setVotosNo(proyecto.getVotosNo() - 1);
                break;
            case BLANCO:
                proyecto.setVotosBlanco(proyecto.getVotosBlanco() - 1);
                break;
        }
    }

    @PostMapping("/votarPorProyecto/{ciudadanoId}/{proyectoId}")
    public ResponseEntity<?> votarPorProyecto(
            @PathVariable Integer ciudadanoId,
            @PathVariable Integer proyectoId,
            @RequestBody VotoRequest votoRequest
    ) {
        // Verificar si el ciudadano existe y está activo
        if (!userRepository.existsByIdAndActivo(ciudadanoId, true)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Su cuenta está deshabilitada por favor habilitela de nuevo.");
        }

        // Obtener los datos del ciudadano para verificar el rol
        User ciudadano = userRepository.findById(ciudadanoId)
                .orElseThrow(() -> new RuntimeException("Ciudadano no encontrado"));

        // Verificar si el usuario es un ciudadano
        if (!ciudadano.getRol().getDescripcion().equalsIgnoreCase("ciudadano")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo los ciudadanos pueden votar.");
        }

        // Verificar si el proyecto existe
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // Verificar si el ciudadano ya ha votado en este proyecto
        Optional<Voto> votoExistente = votoRepository.findByCiudadanoAndProyecto(ciudadano, proyecto);
        if (votoExistente.isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Ya tienes un voto registrado. Solo puedes modificarlo si está activo.");
        }

        // Verificar el estado de la votación
        Procesodevotacion proceso = proyecto.getProcesodevotacion();
        proceso.checkEstado(); // Asegurarse de que el estado esté actualizado
        procesoRepository.save(proceso); // Guardar cambios si el estado cambió

        // Comprobar si el estado es "definido"
        if ("definido".equalsIgnoreCase(proceso.getEstado())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se puede votar aun por favor este hasta " + proceso.getFechadeinicio());
        }

        // Verificar otros posibles estados usando la lógica actual
        ResponseEntity<?> estadoVerificado = verificarEstadoProceso(proceso);
        if (estadoVerificado.getStatusCode() != HttpStatus.OK) {
            return estadoVerificado;
        }

        // Registrar el voto
        Voto nuevoVoto = new Voto(ciudadano, proyecto, votoRequest.getVoto());
        votoRepository.save(nuevoVoto);

        // Actualizar los contadores de votos del proyecto
        actualizarContadoresVoto(proyecto, nuevoVoto);

        return ResponseEntity.ok("Voto registrado exitosamente.");
    }

    @PutMapping("/votoModificar/{ciudadanoId}/{proyectoId}")
    public ResponseEntity<?> votoModificar(
            @PathVariable Integer ciudadanoId,
            @PathVariable Integer proyectoId,
            @RequestBody VotoRequest votoRequest
    ) {
        // Verificar si el ciudadano y el proyecto existen
        User ciudadano = userRepository.findById(ciudadanoId)
                .orElseThrow(() -> new RuntimeException("Ciudadano no encontrado"));
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // Verificar si existe un voto previo del ciudadano para el proyecto
        Voto votoExistente = votoRepository.findByCiudadanoAndProyecto(ciudadano, proyecto)
                .orElseThrow(() -> new RuntimeException("No se encontró un voto previo para modificar"));

        // Verificar el estado de la votación
        Procesodevotacion proceso = proyecto.getProcesodevotacion();
        proceso.checkEstado();

        // Comprobar si el estado es "definido"
        if ("definido".equalsIgnoreCase(proceso.getEstado())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se puede votar aun por favor este hasta " + proceso.getFechadeinicio());
        }

        // Verificar otros posibles estados usando la lógica actual
        ResponseEntity<?> estadoVerificado = verificarEstadoProceso(proceso);
        if (estadoVerificado.getStatusCode() != HttpStatus.OK) {
            return estadoVerificado;
        }

        // Restar el voto anterior
        restarVotoAnterior(proyecto, votoExistente);

        // Actualizar el voto
        votoExistente.setVoto(votoRequest.getVoto());
        votoRepository.save(votoExistente);

        // Actualizar los contadores de votos del proyecto con el nuevo voto
        actualizarContadoresVoto(proyecto, votoExistente);

        return ResponseEntity.ok("Voto modificado exitosamente.");
    }
}
