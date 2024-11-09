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
@RequestMapping("/rol")
public class RolController {
    @Autowired
    private RolRepository rolRepository;
    
    @PostMapping("/adicionarRol")
    public ResponseEntity<?> adicionarRol(@RequestBody Rol rol) {
        if (rolRepository.existsByCodigoderol(rol.getCodigoderol())) {
            return ResponseEntity.badRequest().body("Ya esta en uso ese rol");
        }
        Rol nuevorol = rolRepository.save(rol);
        return ResponseEntity.ok(nuevorol);
    }
    
    @GetMapping("/consultarRol")
    public List<Rol> consultarRol() {
        return rolRepository.findAll();
    }
    
    @PutMapping("/modificarRol/{id}")
    public Rol modificarRol(@PathVariable Integer id, @RequestBody Rol RolDetails) {
        Rol rol = rolRepository.findById(id).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        rol.setCodigoderol(RolDetails.getCodigoderol());
        rol.setDescripcion(RolDetails.getDescripcion());
        rol.setResponsabilidades(RolDetails.getResponsabilidades());
        return rolRepository.save(rol);
    }
    
    @DeleteMapping("/eliminarRol/{id}")
    public String eliminarRol(@PathVariable Integer id) {
        rolRepository.deleteById(id);
        return "Rol eliminado exitosamente";
    }
}
