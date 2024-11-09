package co.edu.unipiloto.edu.proyectoVotos.controller;

import co.edu.unipiloto.edu.proyectoVotos.dao.*;
import co.edu.unipiloto.edu.proyectoVotos.model.*;
import static co.edu.unipiloto.edu.proyectoVotos.model.VotoEnum.*;
import java.time.LocalDateTime;
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
    private TipoRepository tipoRepository;

    @Autowired
    private ProcesoRepository procesoRepository;
    
    @PostMapping("/votarPorProyecto/{ciudadanoId}/{proyectoId}")
    public ResponseEntity<?> votarPorProyecto(
            @PathVariable Integer ciudadanoId,
            @PathVariable Integer proyectoId,
            @RequestBody VotoRequest votoRequest
    ) {
        // Verificar si el usuario es un ciudadano
        User ciudadano = userRepository.findById(ciudadanoId)
                .orElseThrow(() -> new RuntimeException("Ciudadano no encontrado"));

        if (!ciudadano.getRol().getDescripcion().equalsIgnoreCase("ciudadano")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo los ciudadanos pueden votar.");
        }

        // Encontrar el proyecto basado en el ID proporcionado
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // Verificar el estado de la votación
        Procesodevotacion proceso = proyecto.getProcesodevotacion();
        proceso.checkEstado(); // Asegurarse de que el estado esté actualizado
        procesoRepository.save(proceso); // Guardar cambios si el estado cambió

        LocalDateTime fechaActual = LocalDateTime.now();

        // Verificar si la votación está activa y dentro del periodo permitido
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

        // Registrar el voto
        Voto nuevoVoto = new Voto(ciudadano, proyecto, votoRequest.getVoto());
        votoRepository.save(nuevoVoto);

        // Actualización de los votos en el proyecto
        switch (votoRequest.getVoto()) {
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
        return ResponseEntity.ok("Voto registrado exitosamente.");
    }
    
}
