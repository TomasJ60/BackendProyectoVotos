package co.edu.unipiloto.edu.proyectoVotos.controller;

import co.edu.unipiloto.edu.proyectoVotos.dao.ProcesoRepository;
import co.edu.unipiloto.edu.proyectoVotos.dao.ProyectoRepository;
import co.edu.unipiloto.edu.proyectoVotos.dao.TipoRepository;
import co.edu.unipiloto.edu.proyectoVotos.dao.UserRepository;
import co.edu.unipiloto.edu.proyectoVotos.dao.VotoRepository;
import co.edu.unipiloto.edu.proyectoVotos.model.Procesodevotacion;
import co.edu.unipiloto.edu.proyectoVotos.model.Proyecto;
import co.edu.unipiloto.edu.proyectoVotos.model.Tiposdeproyecto;
import co.edu.unipiloto.edu.proyectoVotos.model.User;
import co.edu.unipiloto.edu.proyectoVotos.model.Voto;
import co.edu.unipiloto.edu.proyectoVotos.model.VotoEnum;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/adicionarProyecto")
    public Proyecto adicionarProyecto(@RequestBody Proyecto proyecto) {
        Integer identificador = proyecto.getTipodeproyecto().getIdentificador();
        Integer identificadorProceso = proyecto.getProcesodevotacion().getIdentificador();
        
        Procesodevotacion procesoExiste = procesoRepository.findByIdentificador(identificadorProceso)
                .orElseThrow(() -> new RuntimeException("Proceso de votación no encontrado"));
        
        // Verifica si el tipo de proyecto existe
        Tiposdeproyecto tipoExiste = tipoRepository.findByIdentificador(identificador)
                .orElseThrow(() -> new RuntimeException("Tipo de proyecto no encontrado")); 

        
        proyecto.setProcesodevotacion(procesoExiste);
        proyecto.setTipodeproyecto(tipoExiste);
        
        return proyectoRepository.save(proyecto);
    }

    @PutMapping("/modificarProyecto/{id}")
    public Proyecto modificarProyecto(@PathVariable Integer id, @RequestBody Proyecto proyectoDetails) {
        Proyecto proyecto = proyectoRepository.findById(id).orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        proyecto.setNombreProyecto(proyectoDetails.getNombreProyecto());
        proyecto.setIdentificador(proyectoDetails.getIdentificador());

        proyecto.setAlcance(proyectoDetails.getAlcance());
        proyecto.setPeriododetiempoprevisto(proyectoDetails.getPeriododetiempoprevisto());
        proyecto.setTipodeproyecto(proyectoDetails.getTipodeproyecto());
        proyecto.setLocalidad(proyectoDetails.getLocalidad());
        proyecto.setPresupuesto(proyectoDetails.getPresupuesto());
        proyecto.setNciudadanos(proyectoDetails.getNciudadanos());
        proyecto.setImpacto(proyectoDetails.getImpacto());
        proyecto.setAspectosasociados(proyectoDetails.getAspectosasociados());
        return proyectoRepository.save(proyecto);
    }

    @DeleteMapping("/eliminarProyecto/{id}")
    public String eliminarProyecto(@PathVariable Integer id) {
        proyectoRepository.deleteById(id);
        return "Proyecto eliminado exitosamente";
    }

    @GetMapping("/consultarProyecto")
    public List<Proyecto> consultarProyecto() {
        return proyectoRepository.findAll();
    }

    @GetMapping("/filtrarProyectosPorLocalidad")
    public List<Proyecto> filtrarProyectosPorLocalidad(@RequestParam String localidad) {
        return proyectoRepository.findByLocalidad(localidad);
    }

    @PostMapping("/votarPorProyecto")
    public Proyecto votarPorProyecto(@RequestParam String nombreProyecto, @RequestParam Integer ciudadanoId, @RequestParam VotoEnum voto) {
        // Verificar si el proyecto existe
        Proyecto proyecto = proyectoRepository.findByNombreProyecto(nombreProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // Verificar si el ciudadano existe
        User ciudadano = userRepository.findById(ciudadanoId)
                .orElseThrow(() -> new RuntimeException("Ciudadano no encontrado"));

        // Crear y guardar el voto
        Voto nuevoVoto = new Voto(ciudadano, proyecto, voto);
        votoRepository.save(nuevoVoto);

        // Actualizar los contadores de votos en el proyecto
        switch (voto) {
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

        // Guardar cambios en el proyecto
        return proyectoRepository.save(proyecto);
    }
}
