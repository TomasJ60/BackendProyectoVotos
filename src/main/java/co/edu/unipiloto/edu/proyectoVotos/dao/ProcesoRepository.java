package co.edu.unipiloto.edu.proyectoVotos.dao;

import co.edu.unipiloto.edu.proyectoVotos.model.*;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.*;

/**
 *
 * @author tomas
 */
public interface ProcesoRepository extends CrudRepository<Procesodevotacion, Integer> {
    List<Procesodevotacion> findAll();
    Optional<Procesodevotacion> findByIdentificador(Integer identificador);
    boolean existsByIdentificador(Integer identificador); 
    
}

