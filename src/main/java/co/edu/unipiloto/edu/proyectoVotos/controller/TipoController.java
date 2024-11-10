package co.edu.unipiloto.edu.proyectoVotos.controller;

import co.edu.unipiloto.edu.proyectoVotos.dao.*;
import co.edu.unipiloto.edu.proyectoVotos.model.*;
import java.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author tomas
 */
@RestController
@RequestMapping("/tiposdeproyecto")
public class TipoController {

    @Autowired
    private TipoRepository tipoRepository;
    
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

    @PostMapping("/adicionarTiposdeproyecto/{usuarioId}")
    public ResponseEntity<?> adicionartipodeproyecto(@RequestBody Tiposdeproyecto tipo, @PathVariable Integer usuarioId) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ResponseEntity<?> respuestaRol = verificarRolPlaneador(usuario);
        if (respuestaRol != null) {
            return respuestaRol;
        }

        if (tipoRepository.existsByIdentificador(tipo.getIdentificador())) {
            return ResponseEntity.badRequest().body("El identificador ya está en uso");
        }

        Tiposdeproyecto nuevoTipo = tipoRepository.save(tipo);
        return ResponseEntity.ok(nuevoTipo);
    }

    @GetMapping("/consultarTiposdeproyecto/{usuarioId}")
    public ResponseEntity<?> consultarTiposdeproyecto(@PathVariable Integer usuarioId) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ResponseEntity<?> respuestaRol = verificarRolPlaneador(usuario);
        if (respuestaRol != null) {
            return respuestaRol;
        }

        List<Tiposdeproyecto> tipos = tipoRepository.findAll();
        return ResponseEntity.ok(tipos);
    }

    @PutMapping("/modificarTiposdeproyecto/{id}/{usuarioId}")
    public ResponseEntity<?> modificarTiposdeproyecto(@PathVariable Integer id, @RequestBody Tiposdeproyecto tiposDetails, @PathVariable Integer usuarioId) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ResponseEntity<?> respuestaRol = verificarRolPlaneador(usuario);
        if (respuestaRol != null) {
            return respuestaRol;
        }

        Tiposdeproyecto tipos = tipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo proyecto no encontrado"));

        tipos.setIdentificador(tiposDetails.getIdentificador());
        tipos.setNombredeltipo(tiposDetails.getNombredeltipo());
        tipos.setDescripcion(tiposDetails.getDescripcion());

        tipoRepository.save(tipos);
        return ResponseEntity.ok(tipos);
    }

    @DeleteMapping("/eliminarTiposdeproyecto/{id}/{usuarioId}")
    public ResponseEntity<?> eliminarTiposdeproyecto(@PathVariable Integer id, @PathVariable Integer usuarioId) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ResponseEntity<?> respuestaRol = verificarRolPlaneador(usuario);
        if (respuestaRol != null) {
            return respuestaRol;
        }

        tipoRepository.deleteById(id);
        return ResponseEntity.ok("Tipo proyecto eliminado exitosamente");
    }
}
