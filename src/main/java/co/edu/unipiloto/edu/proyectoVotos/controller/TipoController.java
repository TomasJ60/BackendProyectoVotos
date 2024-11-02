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
@RequestMapping("/tiposdeproyecto")
public class TipoController {
    
    @Autowired
    private TipoRepository tipoRepository;
    
    @PostMapping("/adicionarTiposdeproyecto")
    public ResponseEntity<?> adicionartipodeproyecto(@RequestBody Tiposdeproyecto tipo) {
        if (tipoRepository.existsByIdentificador(tipo.getIdentificador())) {
            return ResponseEntity.badRequest().body("El identificador ya está en uso");
        }
        
        // Si no existe, guarda el nuevo tipo de proyecto
        Tiposdeproyecto nuevoTipo = tipoRepository.save(tipo);
        return ResponseEntity.ok(nuevoTipo);
    }
    
    @GetMapping("/consultarTiposdeproyecto")
    public List<Tiposdeproyecto> consultarTiposdeproyecto() {
        return tipoRepository.findAll();
    }
    
    @PutMapping("/modificarTiposdeproyecto/{id}")
    public Tiposdeproyecto modificarTiposdeproyecto(@PathVariable Integer id, @RequestBody Tiposdeproyecto tiposDetails) {
        Tiposdeproyecto tipos = tipoRepository.findById(id).orElseThrow(() -> new RuntimeException("Tipo proyecto no encontrado"));
        tipos.setIdentificador(tiposDetails.getIdentificador());
        tipos.setNombredeltipo(tiposDetails.getNombredeltipo());
        tipos.setDescripcion(tiposDetails.getDescripcion());
        return tipoRepository.save(tipos);
    }
    
    @DeleteMapping("/eliminarTiposdeproyecto/{id}")
    public String eliminarTiposdeproyecto(@PathVariable Integer id) {
        tipoRepository.deleteById(id);
        return "Tipo proyecto eliminado exitosamente";
    }
    
    
    //hacer llave foranea a la tabla proyectos al campo tiposdeproyectos
    
    
    
}
