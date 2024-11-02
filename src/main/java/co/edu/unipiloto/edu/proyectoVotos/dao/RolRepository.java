package co.edu.unipiloto.edu.proyectoVotos.dao;

import co.edu.unipiloto.edu.proyectoVotos.model.*;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.*;

/**
 *
 * @author tomas
 */
public interface RolRepository extends CrudRepository<Rol, Integer>{
    List<Rol> findAll();
    Optional<Rol> findByCodigoderol(Integer codigoderol);
    boolean existsByCodigoderol(Integer codigoderol);
    
}
