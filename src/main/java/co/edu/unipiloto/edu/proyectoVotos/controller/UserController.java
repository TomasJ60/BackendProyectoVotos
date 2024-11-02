package co.edu.unipiloto.edu.proyectoVotos.controller;

import co.edu.unipiloto.edu.proyectoVotos.dao.ProyectoRepository;
import co.edu.unipiloto.edu.proyectoVotos.dao.RolRepository;
import co.edu.unipiloto.edu.proyectoVotos.dao.UserRepository;
import co.edu.unipiloto.edu.proyectoVotos.model.Proyecto;
import co.edu.unipiloto.edu.proyectoVotos.model.Rol;
import co.edu.unipiloto.edu.proyectoVotos.model.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Tomas Alvarez
 */
@RestController
@RequestMapping("/ciudadano")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository rolRepository;

    @PostMapping("/registrarCiudadano")
    public User registrarCiudadano(@RequestBody User user) {
        Integer codigoderol = user.getRol().getCodigoderol();

        // Verifica si ya existe un rol con el mismo codigoderol
        if (rolRepository.findByCodigoderol(codigoderol).isPresent()) {
            throw new RuntimeException("Ya se creó ese código de rol"); // Mensaje de error
        }

        // Si no existe, se guarda el nuevo rol
        Rol nuevoRol = new Rol();
        nuevoRol.setCodigoderol(codigoderol);
        // Aquí puedes establecer otros atributos del rol si es necesario

        rolRepository.save(nuevoRol); // Guarda el nuevo rol
        user.setRol(nuevoRol); // Asigna el rol al usuario

        return userRepository.save(user);
    }

    @PutMapping("/actualizarCiudadano/{id}")
    public User actualizarCiudadano(@PathVariable Integer id, @RequestBody User userDetails) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Ciudadano no encontrado"));
        user.setNombre(userDetails.getNombre());
        user.setCorreo(userDetails.getCorreo());
        user.setTelefono(userDetails.getTelefono());
        user.setLocalidad(userDetails.getLocalidad());
        user.setDireccion(userDetails.getDireccion());
        user.setVoto(userDetails.getVoto());
        return userRepository.save(user);
    }

    @PutMapping("/deshabilitarCiudadano/{id}")
    public String deshabilitarCiudadano(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Ciudadano no encontrado"));
        user.setActivo(false);
        userRepository.save(user);
        return "Ciudadano deshabilitado exitosamente";
    }

    @GetMapping("/consultarCiudadanos")
    public List<User> consultarCiudadanos() {
        return userRepository.findAll();
    }

    @PutMapping("/habilitarCiudadano/{id}")
    public String habilitarCiudadano(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Ciudadano no encontrado"));
        user.setActivo(true);
        userRepository.save(user);
        return "Ciudadano habilitado exitosamente";
    }

}
