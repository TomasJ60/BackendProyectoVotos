package co.edu.unipiloto.edu.proyectoVotos.dao;

import co.edu.unipiloto.edu.proyectoVotos.model.Proyecto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author tomas
 */
public interface ProyectoRepository extends CrudRepository<Proyecto, Integer> {
    List<Proyecto> findByLocalidad(String localidad);
    List<Proyecto> findAll();
    Optional<Proyecto> findByNombreProyecto(String nombreProyecto);
    
}

